package com.cjs.cloudsaver.config.response;


import com.cjs.cloudsaver.common.response.CommonHttpResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Nullable;
import jakarta.annotation.Resource;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;

@ControllerAdvice
public class ResponseAdvice implements ResponseBodyAdvice<Object> {

    @Resource
    private ObjectMapper objectMapper;

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(@Nullable Object body, MethodParameter returnType, MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        URI uri = request.getURI();
        // 文件的处理直接返回
        if (body instanceof org.springframework.core.io.Resource) {
            return body;
        }


        Object ret;
        if (body instanceof CommonHttpResponse) {
            ret = body;
        }
        else {
            if (body==null){
                body="";
            }
            ret = CommonHttpResponse.of(body);
        }
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        response.getHeaders().set(HttpHeaders.CONTENT_TYPE, "application/json; charset=UTF-8");

        try {
            //自定义的序列化器
            String json = objectMapper.writeValueAsString(ret);
            IOUtils.copy(new ByteArrayInputStream(json.getBytes()), response.getBody());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // 封装返回值
        return null;
    }
}
