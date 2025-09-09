package com.cjs.cloudsaver.config.security;

import com.alibaba.fastjson2.JSON;
import com.cjs.cloudsaver.common.response.CommonHttpResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

@Component
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {



    /**
     * Called when an authentication attempt fails.
     *
     * @param request   the request during which the authentication attempt occurred.
     * @param response  the response.
     * @param exception the exception which was thrown to reject the authentication
     *                  request.
     */
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException {
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        CommonHttpResponse result = CommonHttpResponse.ofError(HttpServletResponse.SC_UNAUTHORIZED, exception.getMessage());
        PrintWriter writer = response.getWriter();
        writer.print(JSON.toJSONString(result));
        writer.flush();
        writer.close();
    }
}
