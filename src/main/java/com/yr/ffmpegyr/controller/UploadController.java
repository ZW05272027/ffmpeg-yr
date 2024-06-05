package com.yr.ffmpegyr.controller;


import com.alibaba.fastjson2.JSONObject;
import com.yr.ffmpegyr.config.LimitHandler;
import com.yr.ffmpegyr.controller.vo.Result;
import com.yr.ffmpegyr.service.DataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;


/**
 * 接口
 *
 * @projectName: ffmpeg
 * @className: UploadController
 * @author: Mby
 * @date: 2024/6/3 10:54
 * @version: 1.0
 */
@Slf4j
@RestController
@RequestMapping("/f")
public class UploadController {


    @Value("${app.video-folder}")
    private String videoFolder;

    @Autowired
    private DataService dataService;

    @Autowired
    private LimitHandler limitHandler;

    /**
     * 上传视频进行切片处理，返回访问路径
     *
     * @param file 文件
     * @return 任务id
     * @throws IOException io异常
     */
    @PostMapping("/upload")
    public Result<String> upload(@RequestPart(name = "file", required = true) MultipartFile file
    ) throws IOException {
        log.info("文件信息：title={}, size={}", file.getOriginalFilename(), file.getSize());
        if (!limitHandler.isCheck()) {
            throw new RuntimeException("服务器忙碌中！");
        }
        // 原始文件名称，也就是视频的标题
        String title = file.getOriginalFilename();

        try {
            // 删除后缀
            title = title.substring(0, title.lastIndexOf(".")) + "-" + UUID.randomUUID().toString().replaceAll("-", "").substring(0, 8);

            // 按照日期生成子目录
            String today = DateTimeFormatter.ofPattern("yyyyMMdd").format(LocalDate.now());

            // 尝试创建视频目录
            Path targetFolder = Files.createDirectories(Paths.get(videoFolder, today, title));
            log.info("创建文件夹目录：{}", targetFolder);
            Files.createDirectories(targetFolder);
            Path tempFile = targetFolder.resolve(title);
            file.transferTo(tempFile);
            // 执行抽帧
            log.info("开始抽帧");
            String taskId = dataService.submit(tempFile.toString(), targetFolder.toString());

            //返回数据
            return Result.success(taskId);
        } finally {

        }
    }

    /**
     * 查询结果
     *
     * @param taskId 任务id
     * @return 结果值
     */
    @GetMapping("/progress")
    public Result<Object> progress(@RequestParam("taskId") String taskId) {
        Object progress = dataService.getProgress(taskId);
        log.debug("进度：{}", JSONObject.toJSONString(progress));
        return Result.success(progress);
    }
}
