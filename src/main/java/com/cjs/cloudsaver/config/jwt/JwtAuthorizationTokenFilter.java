package com.cjs.cloudsaver.config.jwt;

import com.alibaba.fastjson2.JSON;
import com.cjs.cloudsaver.common.exception.BizException;
import com.cjs.cloudsaver.common.response.CommonHttpResponse;
import com.cjs.cloudsaver.config.security.AppUserDetailService;
import com.cjs.cloudsaver.config.security.WebSecurityConfig;
import com.cjs.cloudsaver.model.account.AccountUser;
import com.cjs.cloudsaver.service.account.AccountUserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * JWT 登录认证 filter
 */
@Component
@Slf4j
public class JwtAuthorizationTokenFilter extends OncePerRequestFilter {

    private final JWTUtils jwtUtils;



    private final MessageSource messageSource;

    private final AppUserDetailService appUserDetailService;

    private final AccountUserService accountUserService;



    public JwtAuthorizationTokenFilter(JWTUtils jwtUtils,
                                       MessageSource messageSource,
                                       AppUserDetailService appUserDetailService,
                                       AccountUserService accountUserService) {
        this.jwtUtils = jwtUtils;
        this.messageSource = messageSource;
        this.appUserDetailService = appUserDetailService;
        this.accountUserService = accountUserService;
    }


    /**
     * 对于白名单中的请求不进行过滤
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return Arrays.stream(WebSecurityConfig.AUTH_WHITE_LIST)
                .anyMatch(s -> new AntPathMatcher().match(s, request.getRequestURI()));
    }

    /**
     * JWT token 认证
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        final String authHeader = request.getHeader(jwtUtils.getHeader());
        String token = authHeader;
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
        }
        //自动登录admin账户
//        final String token = jwtUtils.generateToken("admin");
        // 获取请求的 URI（不包含域名和参数）
        String requestURI = request.getRequestURI();

        String userId = null;

        if (StringUtils.hasText(token)) {
            try {
                userId = jwtUtils.parseToken(token);
            } catch (BizException e) {
                log.error("JWT 认证失败", e);
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setCharacterEncoding(StandardCharsets.UTF_8.name());
                response.getWriter().write(messageSource.getMessage(e.getMessage(), null, request.getLocale()));
                return;
            }
        }

        // 如果 token 解析正常 将用户信息存入 SecurityContext
        if (StringUtils.hasText(userId) ) {
            AccountUser user = accountUserService.findByUserId(userId);
            UserDetails userDetails = appUserDetailService.loadUserByUsername(user.getUsername());
            if (!userDetails.isEnabled()) {
                log.error("用户登录失败：用户被禁用");
                response.setStatus(HttpServletResponse.SC_OK);
                response.setCharacterEncoding(StandardCharsets.UTF_8.name());
                CommonHttpResponse result = CommonHttpResponse.ofError(4001,  messageSource.getMessage("auth_user_disabled", null,request.getLocale()));
                response.getWriter().write(JSON.toJSONString(result));
                return;
            }
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        else {
            log.error("用户登录失败：token 无效或缓存中不存在该用户信息:"+requestURI);
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
            return;
        }

        filterChain.doFilter(request, response);
    }
}
