package com.yr.ffmpegyr.config;

import com.yr.ffmpegyr.controller.vo.Result;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

/**
 * 类功能描述
 *
 * @projectName: ffmpeg-yr
 * @className: GlobalExceptionHandler
 * @author: Mby
 * @date: 2024/6/4 16:29
 * @version: 1.0
 */
@ControllerAdvice
public class GlobalExceptionHandler {
    // Handle global exceptions
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGlobalException(Exception ex, WebRequest request) {
        return new ResponseEntity<>(Result.builder().success(false).msg(ex.getMessage()).code("500").build()
                , HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
