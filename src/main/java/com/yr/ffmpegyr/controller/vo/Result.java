package com.yr.ffmpegyr.controller.vo;

import lombok.Builder;
import lombok.Data;

/**
 * 结果
 *
 * @projectName: ffmpeg-yr
 * @className: Result
 * @author: Mby
 * @date: 2024/6/3 18:29
 * @version: 1.0
 */
@Data
@Builder
public  class  Result <T>{
    private Boolean success;
    private String msg;
    private String code;
    private T data;
    public static <T> Result<T> success(T data){
        return Result.<T>builder().success(true).code("200").data(data).build();
    }
}
