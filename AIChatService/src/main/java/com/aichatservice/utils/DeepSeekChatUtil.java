package com.aichatservice.utils;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.*;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;
import okhttp3.sse.EventSources;
import org.jetbrains.annotations.NotNull;

/**
 * @author tengyue
 * @date 2025/6/27/23:49
 */
public class DeepSeekChatUtil{

    /**
     * 流式调用DeepSeek API并实时处理返回结果
     * @param model 使用的模型
     * @param API_URL 请求地址
     * @param API_KEY 密钥
     * @param message 问题信息
     */
    public static void streamChatCompletion(String model,String API_URL,String API_KEY,String message) {
        OkHttpClient client = new OkHttpClient();

        // 构建请求体
        String requestBody = "{\n" +
                "  \"model\": \""+model+"\",\n" +
                "  \"messages\": [\n" +
                "    {\"role\": \"user\", \"content\": \""+message+"\"}\n" +
                "  ],\n" +
                "  \"stream\": true\n" +
                "}";

        Request request = new Request.Builder()
                .url(API_URL)
                .post(RequestBody.create(requestBody, MediaType.parse("application/json")))
                .addHeader("Authorization", "Bearer " + API_KEY)
                .build();

        // 创建EventSource监听器
        EventSourceListener listener = new EventSourceListener() {
            @Override
            public void onEvent(EventSource eventSource, String id, String type, String data) {
                if (data.equals("[DONE]")) {
                    return;
                }
                // 这里可以解析JSON并提取内容
                JsonObject jsonResponse = JsonParser.parseString(data).getAsJsonObject();
                if (jsonResponse.has("choices")) {
                    String content = jsonResponse.getAsJsonArray("choices")
                            .get(0).getAsJsonObject()
                            .getAsJsonObject("delta")
                            .get("content").getAsString();
                    System.out.print(content);
                }
            }

            @Override
            public void onFailure(@NotNull EventSource eventSource, Throwable t, Response response) {
                if (t != null) {
                    System.err.println("连接错误: " + t.getMessage());
                }
                if (response != null) {
                    System.err.println("响应结果: " + response.code() + " " + response.message());
                }
            }
        };

        // 创建并启动EventSource
        EventSources.createFactory(client)
                .newEventSource(request, listener);
    }
}
