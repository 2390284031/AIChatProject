package com.aichatservice;

import com.aichatservice.utils.DeepSeekChatUtil;
import com.aichatservice.utils.DeepSeekProperties;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.RestTemplate;

@SpringBootTest
class AiChatServiceApplicationTests {

    @Autowired
    DeepSeekProperties deepSeekProperties;

    @Test
    void contextLoads() {
        System.out.println(deepSeekProperties);
        DeepSeekChatUtil.streamChatCompletion(deepSeekProperties.getMODEL(), deepSeekProperties.getAPI_URL(),
                deepSeekProperties.getAPI_KEY(), "你是谁");
        try {
            Thread.sleep(10000); // 等待10秒
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
