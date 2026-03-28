package com.wms.aspect;

import com.wms.annotation.RedisLock;
import com.wms.exception.BusinessException;
import com.wms.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

/**
 * Redis分布式锁切面
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class RedisLockAspect {

    private final StringRedisTemplate redisTemplate;
    private final SpelExpressionParser parser = new SpelExpressionParser();
    private final DefaultParameterNameDiscoverer discoverer = new DefaultParameterNameDiscoverer();

    @Around("@annotation(redisLock)")
    public Object around(ProceedingJoinPoint point, RedisLock redisLock) throws Throwable {
        String lockKey = generateKey(point, redisLock);
        String fullKey = redisLock.prefix() + lockKey;

        Boolean locked = redisTemplate.opsForValue()
                .setIfAbsent(fullKey, "1", redisLock.expire(), redisLock.unit());

        if (!Boolean.TRUE.equals(locked)) {
            log.warn("获取分布式锁失败: {}", fullKey);
            throw new BusinessException(ErrorCode.ERROR.getCode(), redisLock.message());
        }

        try {
            log.debug("获取分布式锁成功: {}", fullKey);
            return point.proceed();
        } finally {
            redisTemplate.delete(fullKey);
            log.debug("释放分布式锁: {}", fullKey);
        }
    }

    /**
     * 生成锁key
     */
    private String generateKey(ProceedingJoinPoint point, RedisLock redisLock) {
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();

        // 设置SpEL上下文
        EvaluationContext context = new StandardEvaluationContext();
        String[] paramNames = discoverer.getParameterNames(method);
        Object[] args = point.getArgs();
        if (paramNames != null) {
            for (int i = 0; i < paramNames.length; i++) {
                context.setVariable(paramNames[i], args[i]);
            }
        }

        return parser.parseExpression(redisLock.key()).getValue(context, String.class);
    }
}
