package com.yr.ffmpegyr.service;

import cn.hutool.cache.CacheUtil;
import cn.hutool.cache.impl.FIFOCache;
import cn.hutool.cache.impl.TimedCache;
import cn.hutool.core.lang.generator.UUIDGenerator;
import com.yr.ffmpegyr.config.LimitHandler;
import com.yr.ffmpegyr.controller.vo.AiResult;
import com.yr.ffmpegyr.controller.vo.ChartVO;
import com.yr.ffmpegyr.controller.vo.TaskProgress;
import com.yr.ffmpegyr.util.FFmpegUtils;
import com.yr.ffmpegyr.util.UploadUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * 服务
 *
 * @projectName: ffmpeg-yr
 * @className: DataService
 * @author: Mby
 * @date: 2024/6/3 18:15
 * @version: 1.0
 */
@Slf4j
@Service
public class DataService {
    final FIFOCache<String, Integer> keyCache;
    final TimedCache<String, ChartVO> valueCache;
    @Autowired
    @Qualifier("taskExecutor")
    private Executor taskExecutor;
    private final Integer cacheMax;
    @Value("${upload.count:25}")
    private Integer uploadCount;
    @Value("${upload.batch:2}")
    private Integer uploadBatch;
    @Value("${upload.url:https://detect.91jzx.cn/camera_vision_api/camera_vision_v2/predict}")
    private String uploadUrl;
    @Autowired
    private LimitHandler limitHandler;
    public DataService(@Value("${cache.max:50}")int cacheMax) {
        keyCache = CacheUtil.newFIFOCache(cacheMax);
        valueCache = CacheUtil.newTimedCache(10*60*1000);
        this.cacheMax = cacheMax;
    }

    private static final String TASK_CACHE_KEY = "taskId:";
    public String submit(String source, String destFolder){
        UUIDGenerator uuidGenerator = new UUIDGenerator();
        String taskId = uuidGenerator.next().replace("-", "");
        synchronized (keyCache) {
            if (keyCache.size() + 1 >= cacheMax) {
                throw new RuntimeException("服务器忙碌，请稍后再试！");
            }
            keyCache.put(TASK_CACHE_KEY + taskId, 0);
        }

        try {
            taskExecutor.execute(()->{
                limitHandler.increment();
                String dir = null;
                try {
                    dir = FFmpegUtils.transcodeToImg(source, destFolder);
                    // 创建 File 对象
                    File directory = new File(dir);

                    // 检查是否是文件夹
                    if (directory.isDirectory()) {
                        // 获取文件夹下的所有文件和文件夹的名称
                        List<String> filesList = Stream.of(Objects.requireNonNull(directory.list()))
                                .filter(fileName -> fileName.endsWith(".png")).collect(Collectors.toList());

                        int size = uploadBatch * uploadCount;
                        int schedule =filesList.size()/size+(filesList.size()%size==0?0:1);
                        // 更新进度
                        for(int i = 0; i < schedule; i++){

                            List<String> uploadList = filesList.subList(i * size, i != schedule - 1 ? (i + 1) * size : filesList.size());
                            List<AiResult> analyses = UploadUtil.analyse(uploadUrl,dir, uploadList, AiResult.class);
                            assert analyses != null;
                            setSchedule(taskId,i,schedule,merge(analyses));
                        }

                    } else {
                        log.error("文件异常");
                        keyCache.put(TASK_CACHE_KEY+taskId,-1);
                        valueCache.remove(TASK_CACHE_KEY+taskId);
                    }
                }  catch (Exception e) {
                    log.error("上传失败",e);
                    throw new RuntimeException(e);
                }finally {
                    keyCache.put(TASK_CACHE_KEY+taskId,100);
                    if(StringUtils.isNotEmpty(dir)) {
                        File directory = new File(destFolder);
                        if (directory.exists()) {
                            deleteDirectory(directory);
                        }
                    }
                    limitHandler.decrement();
                }
            });
        } catch (Exception e) {
            log.error("线程错误",e);
            throw new RuntimeException(e);
        }


        return taskId;
    }


    // 递归删除目录及其内容的方法
    private static void deleteDirectory(File directory) {
        if (!directory.exists()) {
            return;
        }

        // 如果是文件，则直接删除
        if (directory.isFile()) {
            directory.delete();
            return;
        }

        // 如果是目录，则递归删除其内容
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                deleteDirectory(file);
            }
        }

        // 删除空目录
        directory.delete();
    }
    private ChartVO merge(List<AiResult> analyses) {
        ChartVO chartVO = new ChartVO();
        chartVO.setAttentions(new ArrayList<>());
        for (int i = 0; i < analyses.size(); i++){
            chartVO.getAttentions().add(analyses.get(i).getResults().getAttention().getAttentive().multiply(BigDecimal.valueOf(100)));
        }
        return chartVO;
    }

    private ChartVO merge(ChartVO a ,ChartVO b){
        a.getAttentions().addAll(b.getAttentions());
        return a;
    }

    public Object getProgress(String taskId){
        Integer i = keyCache.get(TASK_CACHE_KEY + taskId);
        if (i == null){
            return null;
        }
        if (i == 100){
            return TaskProgress.toFinish(valueCache.get(TASK_CACHE_KEY+taskId));
        }
        if (i == -1){
            return TaskProgress.toBad();
        }
        return TaskProgress.toProgress(i);
    }

    public void delete(String taskId){
        keyCache.remove(TASK_CACHE_KEY+taskId);
        valueCache.remove(TASK_CACHE_KEY+taskId);
    }
    public void setSchedule(String taskId,Integer position,Integer schedule,ChartVO analyse) {
        synchronized (keyCache) {
            keyCache.put(TASK_CACHE_KEY+taskId, position*100/schedule);
            ChartVO base = valueCache.get(TASK_CACHE_KEY + taskId);
            if(Objects.isNull(base)){
                base = ChartVO.builder().attentions(new ArrayList<>()).build();
            }
            merge(base,analyse);
            valueCache.put(TASK_CACHE_KEY + taskId, base);

        }
    }
}
