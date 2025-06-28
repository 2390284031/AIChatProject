package com.aichatservice.utils;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author tengyue
 * @date 2025/6/28/1:42
 */
@Component
@ConfigurationProperties(prefix = "deepseek")
@Data
public class DeepSeekProperties {
    private String MODEL;
    private String API_URL;
    private String API_KEY;
}
