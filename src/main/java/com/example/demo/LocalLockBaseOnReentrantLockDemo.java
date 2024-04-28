package com.example.demo;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

/**
 * [Local Lock] This is an example of using {@link ReentrantLock} To Lock the tread process
 * Pros : Simple to use
 * Cron : Can't cross different service
 */
@Log4j2
@AllArgsConstructor
public class LocalLockBaseOnReentrantLockDemo {

    private static final Map<String, ReentrantLock> locks = new ConcurrentHashMap<>();

    /**
     * Lock The Tread By Process, if not exist lock , if not display error
     */
    public static void lock(String key) {
        // ReentrantLock have constructor can generate FairSync or NonfairSync
        ReentrantLock lock = locks.computeIfAbsent(key, k -> new ReentrantLock(true));

        if (lock.tryLock()) {
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
                lock.unlock();
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
