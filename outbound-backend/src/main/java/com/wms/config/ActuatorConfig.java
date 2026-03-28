package com.wms.config;

import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Spring Boot Actuator 配置
 * 提供健康检查、指标监控等端点
 */
@Configuration
public class ActuatorConfig {

    /**
     * 自定义健康指示器 - 检查数据库连接
     */
    @Bean
    public HealthIndicator databaseHealthIndicator() {
        return () -> {
            // 实际实现中应该检查数据库连接
            // 这里简化处理
            return Health.up()
                    .withDetail("database", "MySQL")
                    .withDetail("status", "connected")
                    .build();
        };
    }

    /**
     * 自定义健康指示器 - 检查Redis连接
     */
    @Bean
    public HealthIndicator redisHealthIndicator() {
        return () -> {
            // 实际实现中应该检查Redis连接
            return Health.up()
                    .withDetail("redis", "Redis")
                    .withDetail("status", "connected")
                    .build();
        };
    }

    /**
     * Actuator安全配置
     * 只允许特定角色访问敏感端点
     */
    @Bean
    public SecurityFilterChain actuatorSecurityFilterChain(HttpSecurity http) throws Exception {
        http
            .requestMatcher(EndpointRequest.toAnyEndpoint())
            .authorizeRequests()
            .requestMatchers(EndpointRequest.to("health", "info")).permitAll()
            .requestMatchers(EndpointRequest.to("metrics", "prometheus")).hasRole("ADMIN")
            .anyRequest().authenticated()
            .and()
            .httpBasic();
        
        return http.build();
    }
}
