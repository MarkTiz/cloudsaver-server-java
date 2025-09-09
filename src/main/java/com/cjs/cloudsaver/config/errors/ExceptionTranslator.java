package com.cjs.cloudsaver.config.errors;

import com.cjs.cloudsaver.common.exception.BizException;
import com.cjs.cloudsaver.common.response.CommonHttpResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Locale;

/**
 * 后台异常处理
 */
@Slf4j
@ControllerAdvice
public class ExceptionTranslator {

    private final MessageSource messageSource;

    public ExceptionTranslator(MessageSource messageSource) {
        this.messageSource = messageSource;
    }


    @ExceptionHandler(BizException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public CommonHttpResponse processBizException(BizException ex) {
        log.error(ex.getMessage(), ex);
        String message = messageSource.getMessage(ex.getMessage(), ex.getArgs(), Locale.CHINA);
        return CommonHttpResponse.ofError(ex.getCode(), message);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public CommonHttpResponse processException(Exception ex) {
        log.error(ex.getMessage(), ex);
        String message = messageSource.getMessage("common_inner_exception", new String[]{ex.getMessage()}, Locale.CHINA);
        return CommonHttpResponse.ofError(message);
    }




}
