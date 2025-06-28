package com.aichatservice;

import com.aichatservice.utils.DeepSeekChatUtil;
import com.aichatservice.utils.DeepSeekProperties;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.*;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;
import okhttp3.sse.EventSources;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

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
