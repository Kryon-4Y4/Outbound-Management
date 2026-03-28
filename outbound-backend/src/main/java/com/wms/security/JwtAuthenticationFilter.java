package com.wms.security;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.wms.constant.SystemConstant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * JWT认证过滤器
 * 拦截请求，验证JWT Token
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenUtil jwtTokenUtil;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                    HttpServletResponse response, 
                                    FilterChain filterChain) throws ServletException, IOException {
        
        // 获取请求头中的Authorization
        String header = request.getHeader(SystemConstant.TOKEN_HEADER);
        
        // 如果没有token或格式不正确，直接放行（后续由其他过滤器处理）
        if (header == null || !header.startsWith(SystemConstant.TOKEN_PREFIX)) {
            filterChain.doFilter(request, response);
            return;
        }

        // 提取token
        String token = header.substring(SystemConstant.TOKEN_PREFIX.length());
        
        // 验证token
        DecodedJWT decodedJWT = jwtTokenUtil.verifyToken(token);
        
        if (decodedJWT != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            String username = jwtTokenUtil.getUsernameFromToken(token);
            
            if (username != null) {
                // 加载用户信息
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
                
                // 创建认证对象
                UsernamePasswordAuthenticationToken authentication = 
                    new UsernamePasswordAuthenticationToken(
                        userDetails, 
                        null, 
                        userDetails.getAuthorities()
                    );
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                
                // 设置认证信息到上下文
                SecurityContextHolder.getContext().setAuthentication(authentication);
                
                log.debug("JWT认证成功，用户名: {}", username);
            }
        }
        
        filterChain.doFilter(request, response);
    }
}
