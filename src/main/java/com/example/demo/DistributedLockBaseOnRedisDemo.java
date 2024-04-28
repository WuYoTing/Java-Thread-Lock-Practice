package com.example.demo;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

import java.util.List;

/**
 * [Distributed Lock] This is an example of using Redis To Lock the tread process
 * Pros : cross different service
 * Cron : Need to measure more situation
 */
@Log4j2
@AllArgsConstructor
public class DistributedLockBaseOnRedisDemo {

    /**
     * Can't use @Value to inject properties,because it's not a spring bean
     */
    private static String host = "localhost";
    private static String port = "6379";
    private static String password = "root";

    public static void lock(RedissonClient redisson, String key) {
        RLock lock = redisson.getFairLock(key);

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

        RedissonClient redisson = createRedissonClient();

        keyList.parallelStream().forEach(key -> {

            Thread thread = new Thread(() -> {
                log.info("Process Thread: {}, key: {}", Thread.currentThread().getName(), key);
                lock(redisson, key);
            });
            thread.start();
        });
    }

    private static RedissonClient createRedissonClient() {
        Config config = new Config();
        config.useSingleServer().setAddress("redis://" + host + ":" + port).setPassword(password);
        return Redisson.create(config);
    }
}
