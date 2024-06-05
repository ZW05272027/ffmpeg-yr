package com.yr.ffmpegyr.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 线程池管理
 *
 * @projectName: ffmpeg-yr
 * @className: ThreadPoolConfig
 * @author: Mby
 * @date: 2024/6/3 17:08
 * @version: 1.0
 */
@Configuration
public class ThreadPoolConfig {

    @Bean(name = "taskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        Runtime runtime = Runtime.getRuntime();
        executor.setCorePoolSize(2*runtime.availableProcessors());
        executor.setMaxPoolSize(4*runtime.availableProcessors());
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("taskExecutor-");
        executor.initialize();
        return executor;
    }
}
