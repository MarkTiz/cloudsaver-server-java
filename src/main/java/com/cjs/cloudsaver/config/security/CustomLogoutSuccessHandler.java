package com.cjs.cloudsaver.config.security;

import com.alibaba.fastjson2.JSON;
import com.cjs.cloudsaver.common.response.CommonHttpResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
public class CustomLogoutSuccessHandler implements LogoutSuccessHandler {



    public CustomLogoutSuccessHandler( ) {
    }

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        // 返回 200 编码
        response.setStatus(HttpServletResponse.SC_OK);

        if (authentication != null) {
            // 获取名称
            AppUserDetails user = (AppUserDetails) authentication.getPrincipal();
            // 移除登录状态
//            commonUserStatusService.delete(user.getLoginName());
        }

        // 写入成功响应
        CommonHttpResponse res = CommonHttpResponse.of();
        response.setStatus(HttpServletResponse.SC_OK);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.getWriter().write(JSON.toJSONString(res));
    }
}
