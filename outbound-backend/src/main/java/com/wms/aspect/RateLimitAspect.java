package com.wms.aspect;

import com.google.common.util.concurrent.RateLimiter;
import com.wms.annotation.RateLimit;
import com.wms.config.RateLimitConfig;
import com.wms.exception.BusinessException;
import com.wms.security.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 限流切面
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class RateLimitAspect {

    private final ConcurrentHashMap<String, RateLimiter> userRateLimiters;
    private final ConcurrentHashMap<String, RateLimiter> ipRateLimiters;
    private final JwtTokenUtil jwtTokenUtil;

    @Around("@annotation(rateLimit)")
    public Object around(ProceedingJoinPoint point, RateLimit rateLimit) throws Throwable {
        RateLimiter rateLimiter = getRateLimiter(rateLimit);
        
        if (!rateLimiter.tryAcquire()) {
            log.warn("请求被限流拦截: {}", rateLimit.type());
            throw new BusinessException(429, rateLimit.message());
        }
        
        return point.proceed();
    }

    /**
     * 获取对应的限流器
     */
    private RateLimiter getRateLimiter(RateLimit rateLimit) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder
                .getRequestAttributes()).getRequest();
        
        switch (rateLimit.type()) {
            case USER:
                String userId = getCurrentUserId(request);
                return RateLimitConfig.getUserRateLimiter(
                    userRateLimiters, 
                    userId, 
                    rateLimit.permitsPerSecond()
                );
                
            case IP:
                String ip = getClientIp(request);
                return RateLimitConfig.getIpRateLimiter(
                    ipRateLimiters,
                    ip,
                    rateLimit.permitsPerSecond()
                );
                
            case GLOBAL:
            case CUSTOM:
            default:
                // 全局限流器，每次创建新的（简化实现）
                return RateLimiter.create(rateLimit.permitsPerSecond());
        }
    }

    /**
     * 获取当前用户ID
     */
    private String getCurrentUserId(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            Long userId = jwtTokenUtil.getUserIdFromToken(token);
            return userId != null ? String.valueOf(userId) : "anonymous";
        }
        return "anonymous";
    }

    /**
     * 获取客户端IP
     */
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // 多个代理情况，取第一个IP
        if (ip != null && ip.contains(",")) {
            ip = ip.substring(0, ip.indexOf(","));
        }
        return ip;
    }
}
