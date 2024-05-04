package com.example.demo;

import com.example.demo.service.LockLogService;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * [Distributed Lock] This is an example of using Database To Lock the tread process
 * Pros :
 * 1. cross different service or pod
 * Cron :
 * 1. Need to measure more situation
 * 2. DB is a remote server, so once we lose connection with Redis, our local thread in the wait queue is blocked till db connection recover
 */
@Log4j2
@Component
@AllArgsConstructor
public class DistributedLockBaseOnDatabaseDemo {


    private static void lock(String key) {
        // 創建 LockService 的實例
        LockLogService lockLogService = new LockLogService();

        if (lockLogService.tryLock(key)) {
            try {
                log.info("Thread: {} , acquired the lock for key: {}", Thread.currentThread().getName(), key);

                // Simulate work for a period
                Thread.sleep(2000);

            } catch (Exception e) {
                // re interrupt thread process
                Thread.currentThread().interrupt();
                log.error("Thread: {}, interrupted: {}", Thread.currentThread().getName(), e.getMessage());
            } finally {
                // Prevent deadlock
                lockLogService.unlock(key);
                log.info("Thread: {}, released the lock for key: {}", Thread.currentThread().getName(), key);
            }
        } else {
            log.error("Thread: {},failed to acquire the lock for key: {}", Thread.currentThread().getName(), key);
        }
    }

    public static void main(String[] args) {
        // Create multiple threads to concurrently execute lock acquisition and release operations
        List<String> keyList = List.of("A", "B", "C", "D", "E", "F", "G", "H", "I", "A");

        keyList.parallelStream().forEach(key -> {

            Thread thread = new Thread(() -> {
                log.info("Process Thread: {}, key: {}", Thread.currentThread().getName(), key);
                lock(key);
            });
            thread.start();
        });
    }
}
