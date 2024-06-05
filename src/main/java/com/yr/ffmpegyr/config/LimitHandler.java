package com.yr.ffmpegyr.config;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 类功能描述
 *
 * @projectName: ffmpeg-yr
 * @className: Bucket4jHandler
 * @author: Mby
 * @date: 2024/6/4 16:57
 * @version: 1.0
 */
@Slf4j
@Service
public class LimitHandler {

    private int count = 0;
    @Value("${upload.maxLimit:2}")
    private int max ;
    private final Lock lock = new ReentrantLock();

    public void increment() {
        lock.lock();
        try {
            count++;
        } finally {
            lock.unlock();
        }
    }

    public boolean isCheck() {
        lock.lock();
        try {
            return max>count;
        } finally {
            lock.unlock();
        }

    }

    public void decrement() {
        lock.lock();
        try {
            count--;
        } finally {
            lock.unlock();
        }
    }
}
