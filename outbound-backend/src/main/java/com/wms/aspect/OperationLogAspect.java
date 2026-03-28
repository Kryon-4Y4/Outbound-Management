package com.wms.aspect;

import com.alibaba.fastjson2.JSON;
import com.wms.annotation.OperationLog;
import com.wms.entity.SysOperationLog;
import com.wms.security.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.time.LocalDateTime;

/**
 * 操作日志切面
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class OperationLogAspect {

    private final JwtTokenUtil jwtTokenUtil;
    // private final SysOperationLogMapper logMapper; // 需要时注入

    @Around("@annotation(operationLog)")
    public Object around(ProceedingJoinPoint point, OperationLog operationLog) throws Throwable {
        // 获取请求信息
        ServletRequestAttributes attributes = (ServletRequestAttributes) 
            RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        
        // 获取方法信息
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        
        // 获取用户信息
        String token = request.getHeader("Authorization");
        Long userId = null;
        String username = "anonymous";
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            userId = jwtTokenUtil.getUserIdFromToken(token);
            username = jwtTokenUtil.getUsernameFromToken(token);
        }

        // 记录开始时间
        long startTime = System.currentTimeMillis();
        
        // 记录请求参数
        String params = null;
        if (operationLog.recordParams()) {
            params = JSON.toJSONString(point.getArgs());
            // 限制长度
            if (params.length() > 2000) {
                params = params.substring(0, 2000) + "...";
            }
        }

        // 执行目标方法
        Object result = null;
        Exception exception = null;
        try {
            result = point.proceed();
            return result;
        } catch (Exception e) {
            exception = e;
            throw e;
        } finally {
            // 计算执行时间
            long duration = System.currentTimeMillis() - startTime;
            
            // 构建日志对象
            SysOperationLog operationLogEntity = new SysOperationLog();
            operationLogEntity.setUserId(userId);
            operationLogEntity.setUsername(username);
            operationLogEntity.setOperation(operationLog.type().name() + ":" + operationLog.desc());
            operationLogEntity.setMethod(method.getDeclaringClass().getName() + "." + method.getName());
            operationLogEntity.setParams(params);
            operationLogEntity.setIp(getClientIp(request));
            operationLogEntity.setDuration(duration);
            operationLogEntity.setStatus(exception == null ? 1 : 0);
            
            if (exception != null) {
                operationLogEntity.setErrorMsg(exception.getMessage());
            }
            
            // 记录响应结果
            if (operationLog.recordResult() && result != null) {
                String resultStr = JSON.toJSONString(result);
                if (resultStr.length() > 2000) {
                    resultStr = resultStr.substring(0, 2000) + "...";
                }
                // operationLogEntity.setResult(resultStr);
            }
            
            operationLogEntity.setCreateTime(LocalDateTime.now());
            
            // 保存日志（异步保存更佳）
            saveLog(operationLogEntity);
            
            log.debug("操作日志记录: {} - {}ms", operationLogEntity.getOperation(), duration);
        }
    }

    /**
     * 保存日志
     */
    private void saveLog(SysOperationLog logEntity) {
        // 异步保存到数据库
        // new Thread(() -> logMapper.insert(logEntity)).start();
        
        // 目前仅记录到日志
        log.info("[操作日志] 用户:{} 操作:{} 方法:{} 耗时:{}ms 状态:{}",
            logEntity.getUsername(),
            logEntity.getOperation(),
            logEntity.getMethod(),
            logEntity.getDuration(),
            logEntity.getStatus() == 1 ? "成功" : "失败"
        );
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
        if (ip != null && ip.contains(",")) {
            ip = ip.substring(0, ip.indexOf(","));
        }
        return ip;
    }
}
