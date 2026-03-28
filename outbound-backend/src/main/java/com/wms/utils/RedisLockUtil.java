package com.wms.utils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * Redis分布式锁工具类
 * 用于打包流程的并发控制，防止多个打包员同时操作同一货单/产品
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RedisLockUtil {

    private final StringRedisTemplate redisTemplate;

    /**
     * 锁前缀
     */
    private static final String LOCK_PREFIX = "lock:";

    /**
     * 默认锁过期时间（秒）
     */
    private static final long DEFAULT_EXPIRE = 30;

    /**
     * 获取锁
     *
     * @param lockKey 锁key
     * @return 是否获取成功
     */
    public boolean tryLock(String lockKey) {
        return tryLock(lockKey, DEFAULT_EXPIRE, TimeUnit.SECONDS);
    }

    /**
     * 获取锁（带过期时间）
     *
     * @param lockKey 锁key
     * @param expire  过期时间
     * @param unit    时间单位
     * @return 是否获取成功
     */
    public boolean tryLock(String lockKey, long expire, TimeUnit unit) {
        String key = LOCK_PREFIX + lockKey;
        Boolean success = redisTemplate.opsForValue()
                .setIfAbsent(key, Thread.currentThread().getId() + "", expire, unit);
        if (Boolean.TRUE.equals(success)) {
            log.debug("获取锁成功: {}", key);
            return true;
        }
        log.debug("获取锁失败: {}", key);
        return false;
    }

    /**
     * 释放锁
     *
     * @param lockKey 锁key
     */
    public void unlock(String lockKey) {
        String key = LOCK_PREFIX + lockKey;
        Boolean deleted = redisTemplate.delete(key);
        if (Boolean.TRUE.equals(deleted)) {
            log.debug("释放锁成功: {}", key);
        }
    }

    /**
     * 打包锁key生成
     * 格式: packing:{orderId}:{productId}
     */
    public static String getPackingLockKey(Long orderId, Long productId) {
        return String.format("packing:%d:%d", orderId, productId);
    }

    /**
     * 客柜锁key生成
     * 格式: container:{containerId}
     */
    public static String getContainerLockKey(Long containerId) {
        return String.format("container:%d", containerId);
    }

    /**
     * 货单锁key生成
     * 格式: order:{orderId}
     */
    public static String getOrderLockKey(Long orderId) {
        return String.format("order:%d", orderId);
    }
}
