package com.yr.ffmpegyr.controller.vo;

import cn.hutool.json.JSONObject;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import lombok.Builder;
import lombok.Data;

/**
 * 类功能描述
 *
 * @projectName: ffmpeg-yr
 * @className: TaskProgress
 * @author: Mby
 * @date: 2024/6/3 20:20
 * @version: 1.0
 */
@Data
@Builder
public class TaskProgress {
    private Integer schedule;
    private Object value;

    public static TaskProgress toFinish(Object value){
        return TaskProgress.builder().schedule(100).value(value).build();
    }
    public static TaskProgress toProgress(Integer schedule){
        return TaskProgress.builder().schedule(schedule).build();
    }
    public static TaskProgress toBad(){
        return TaskProgress.builder().schedule(-1).build();
    }
}
