package com.example.demo.service;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

@Log4j2
@AllArgsConstructor
public class DemoService {

    private static final Map<String, ReentrantLock> locks = new ConcurrentHashMap<>();

    public static void testThread(String key) {
        ReentrantLock lock = locks.computeIfAbsent(key, k -> new ReentrantLock());

        if (lock.tryLock()) {
            try {
                log.info("Thread: {} , acquired the lock for key: {}", Thread.currentThread().getName(), key);

                // Simulate work for a period
                Thread.sleep(5000);

            } catch (Exception e) {
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

        for (int index = 0; index < keyList.size(); index++) {
            int finalIndex = index;
            Thread thread = new Thread(() -> testThread(keyList.get(finalIndex)));
            thread.start();
        }
    }
}
