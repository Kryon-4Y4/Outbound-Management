package com.wms.annotation;

import java.lang.annotation.*;

/**
 * 限流注解
 * 用于方法级别的限流控制
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RateLimit {

    /**
     * 限流类型
     */
    RateLimitType type() default RateLimitType.GLOBAL;

    /**
     * 每秒允许的请求数
     */
    double permitsPerSecond() default 10.0;

    /**
     * 获取不到令牌时的提示信息
     */
    String message() default "请求过于频繁，请稍后再试";

    /**
     * 限流类型枚举
     */
    enum RateLimitType {
        /**
         * 全局限流
         */
        GLOBAL,
        
        /**
         * 按用户限流
         */
        USER,
        
        /**
         * 按IP限流
         */
        IP,
        
        /**
         * 自定义key限流
         */
        CUSTOM
    }
}
