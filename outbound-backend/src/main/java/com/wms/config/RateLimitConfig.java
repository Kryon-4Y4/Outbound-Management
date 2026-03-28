package com.wms.config;

import com.google.common.util.concurrent.RateLimiter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 限流配置
 * 基于Guava RateLimiter实现接口限流
 */
@Slf4j
@Configuration
public class RateLimitConfig {

    /**
     * 登录接口限流：每秒2个请求
     */
    @Bean
    public RateLimiter loginRateLimiter() {
        return RateLimiter.create(2.0);
    }

    /**
     * 全局API限流：每秒100个请求
     */
    @Bean
    public RateLimiter globalRateLimiter() {
        return RateLimiter.create(100.0);
    }

    /**
     * 用户级别的限流器缓存
     * key: 用户ID，value: 限流器
     */
    @Bean
    public ConcurrentHashMap<String, RateLimiter> userRateLimiters() {
        return new ConcurrentHashMap<>();
    }

    /**
     * IP级别的限流器缓存
     * key: IP地址，value: 限流器
     */
    @Bean
    public ConcurrentHashMap<String, RateLimiter> ipRateLimiters() {
        return new ConcurrentHashMap<>();
    }

    /**
     * 获取或创建用户限流器
     * @param userId 用户ID
     * @param permitsPerSecond 每秒许可数
     * @return 限流器
     */
    public static RateLimiter getUserRateLimiter(
            ConcurrentHashMap<String, RateLimiter> cache, 
            String userId, 
            double permitsPerSecond) {
        return cache.computeIfAbsent(userId, k -> {
            log.info("创建用户限流器: {}, 速率: {}/s", userId, permitsPerSecond);
            return RateLimiter.create(permitsPerSecond);
        });
    }

    /**
     * 获取或创建IP限流器
     * @param ip IP地址
     * @param permitsPerSecond 每秒许可数
     * @return 限流器
     */
    public static RateLimiter getIpRateLimiter(
            ConcurrentHashMap<String, RateLimiter> cache,
            String ip,
            double permitsPerSecond) {
        return cache.computeIfAbsent(ip, k -> {
            log.info("创建IP限流器: {}, 速率: {}/s", ip, permitsPerSecond);
            return RateLimiter.create(permitsPerSecond);
        });
    }
}
