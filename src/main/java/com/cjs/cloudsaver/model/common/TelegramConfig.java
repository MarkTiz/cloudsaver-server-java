package com.cjs.cloudsaver.model.common;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Data
@Configuration
@ConfigurationProperties(prefix = "telegram")
public class TelegramConfig {

    /**
     * telegram基础url
     */
    private String baseUrl;

    /**
     * telegram频道
     */
    private Map<String, String> channels;

    /**
     * 搜索匹配模式
     */
    private Map<String, String> cloudPatterns;
}
