package com.wms.annotation;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * Redis分布式锁注解
 * 用于方法级别的并发控制
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RedisLock {

    /**
     * 锁的key，支持SpEL表达式
     */
    String key();

    /**
     * 锁前缀
     */
    String prefix() default "lock:";

    /**
     * 锁过期时间
     */
    long expire() default 30;

    /**
     * 时间单位
     */
    TimeUnit unit() default TimeUnit.SECONDS;

    /**
     * 获取锁失败时的提示信息
     */
    String message() default "操作过于频繁，请稍后重试";
}
