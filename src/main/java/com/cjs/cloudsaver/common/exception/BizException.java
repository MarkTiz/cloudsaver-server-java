package com.cjs.cloudsaver.common.exception;

import lombok.Getter;

@Getter
public class BizException extends Exception {

    /**
     * 错误编码 默认500
     */
    private final int code;

    /**
     * i18n 错误信息参数
     */
    private final Object[] args;

    public BizException(int code, String messageKey, Object... args) {
        super(messageKey);
        this.code = code;
        this.args = args;
    }

    public BizException(String messageKey) {
        this(500, messageKey);
    }

    public BizException(int code, String messageKey) {
        this(code, messageKey, new Object[]{});
    }

    public BizException(String messageKey, Object... args) {
        this(500, messageKey, args);
    }
}
