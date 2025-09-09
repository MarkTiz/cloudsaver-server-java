package com.cjs.cloudsaver.common.response;

import lombok.Getter;

import java.io.Serializable;

@Getter
public class CommonHttpResponse implements Serializable {

    private CommonHttpResponse() {
    }

    /**
     * 接口返回码，0表示成功，500表示通用失败，或者自定义返回码作特殊处理
     */
    private int code;

    /**
     * 返回描述
     */
    private String message;

    /**
     * 返回请求的主体信息
     */
    private Object data;

    public static  CommonHttpResponse ofCode(Integer code, String message, Object data) {
        CommonHttpResponse responseResult = new CommonHttpResponse();
        responseResult.code = code;
        responseResult.message = message;
        responseResult.data = data;
        return responseResult;
    }

    public static  CommonHttpResponse ofError(Integer code, String message) {
        return ofCode(code, message, null);
    }

    public static  CommonHttpResponse ofError(String message) {
        return ofError(500, message);
    }

    public static  CommonHttpResponse of(Object data) {
        return ofCode(0, null, data);
    }

    public static CommonHttpResponse of() {
        return of(null);
    }
}
