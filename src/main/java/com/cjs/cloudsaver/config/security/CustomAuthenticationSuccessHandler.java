package com.cjs.cloudsaver.config.security;

import com.alibaba.fastjson2.JSON;
import com.cjs.cloudsaver.common.response.CommonHttpResponse;
import com.cjs.cloudsaver.config.jwt.JWTUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final JWTUtils jwtUtils;




    public CustomAuthenticationSuccessHandler(JWTUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }


    /**
     * Called when a user has been successfully authenticated.
     *
     * @param request        the request which caused the successful authentication
     * @param response       the response
     * @param authentication the <tt>Authentication</tt> object which was created during
     *                       the authentication process.
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        // 返回 200 编码
        response.setStatus(HttpServletResponse.SC_OK);

        // 生成 Token
        AppUserDetails user = (AppUserDetails) authentication.getPrincipal();
        String userId = user.getUserId();
        String token = jwtUtils.generateToken(userId);

        Map<String, String> map = new HashMap<>();
        map.put("token", token);
        CommonHttpResponse res = CommonHttpResponse.ofCode(0, null, map);
        // 将 token 写入到 body 里面
        response.setStatus(HttpServletResponse.SC_OK);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        response.getWriter().write(JSON.toJSONString(res));

    }
}
