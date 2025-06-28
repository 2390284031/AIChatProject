package com.aichatservice.websocket;

import com.aichatservice.pojo.SendMessage;
import com.aichatservice.utils.DeepSeekProperties;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;
import okhttp3.sse.EventSources;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import java.util.HashMap;
import java.util.Map;

/**
 * WebSocket服务
 */
@Slf4j
@Component
@ServerEndpoint("/websocket/{email}")
public class WebSocketServer {

    @Autowired
    private DeepSeekProperties deepSeekProperties;

    // 存放会话对象
    private static Map<String, Session> sessionMap = new HashMap<>();

    private static ApplicationContext applicationContext;

    // 解决无法注入问题
    public static void setApplicationContext(ApplicationContext applicationContext) {
        WebSocketServer.applicationContext = applicationContext;
    }

    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(Session session,@PathParam("email")  String email) {
        System.out.println("客户端：" + email + "建立连接");
        sessionMap.put(email, session);
    }

    /**
     * 收到客户端消息后调用的方法
     *
     * @param message 客户端发送过来的消息
     */
    @OnMessage
    public void onMessage(String message) {
        System.out.println("[客户端]:" + message);
        SendMessage sendMessage = JSONObject.parseObject(message, SendMessage.class);

        deepSeekProperties = applicationContext.getBean(DeepSeekProperties.class);
        streamChatCompletion(deepSeekProperties.getMODEL(), deepSeekProperties.getAPI_URL(),
                deepSeekProperties.getAPI_KEY(), sendMessage.getContent(), sendMessage.getSendEmail());
    }

    /**
     * 连接关闭调用的方法
     *
     * @param email
     */
    @OnClose
    public void onClose(@PathParam("email") String email) {
        System.out.println("连接断开:" + email);
        sessionMap.remove(email);
    }

    /**
     * 流式调用DeepSeek API并实时处理返回结果
     * @param model 使用的模型
     * @param API_URL 请求地址
     * @param API_KEY 密钥
     * @param message 问题信息
     * @param sendEmail
     */
    public void streamChatCompletion(String model,String API_URL,String API_KEY,
                                     String message, String sendEmail) {
        OkHttpClient client = new OkHttpClient();

        // 构建请求体
//        String requestBody = "{\n" +
//                "  \"model\": \""+model+"\",\n" +
//                "  \"messages\": [\n" +
//                "    {\"role\": \"user\", \"content\": \""+message+"\"}\n" +
//                "  ],\n" +
//                "  \"stream\": true\n" +
//                "}";
        message = message.replace("\n", "\\n").replace("\"", "\\\"");
        String requestBody = String.format("""
                {
                "model": "%s",
                "messages": [
                    {"role": "user", "content": "%s"}
                  ],
                "stream": true
                }
                """, model, message);

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
                    sendToAllClient("[DONE]", sendEmail);
                    return;
                }
                // 这里可以解析JSON并提取内容
                JsonObject jsonResponse = JsonParser.parseString(data).getAsJsonObject();
                if (jsonResponse.has("choices")) {
                    String content = jsonResponse.getAsJsonArray("choices")
                            .get(0).getAsJsonObject()
                            .getAsJsonObject("delta")
                            .get("content").getAsString();

                    sendToAllClient(content, sendEmail);
                }
            }

            @Override
            public void onFailure(@NotNull EventSource eventSource, Throwable t, Response response) {
                if (t != null) {
                    sendToAllClient("连接错误: " + t.getMessage(), sendEmail);
                    sendToAllClient("[DONE]", sendEmail);
                }
                if (response != null) {
                    sendToAllClient("响应结果: " + response.code() + " " + response.message(), sendEmail);
                    sendToAllClient("[DONE]", sendEmail);
                }
            }
        };

        // 创建并启动EventSource
        EventSources.createFactory(client)
                .newEventSource(request, listener);
    }

    /**
     * 向指定人发送信息
     * @param message
     * @param sendEmail
     */
    public void sendToAllClient(String message, String sendEmail) {
        // 不等于null则向指定人发送信息
        if (sendEmail != null){
            Session session = sessionMap.get(sendEmail);
            if (session != null){
                try {
                    SendMessage sendMessage = new SendMessage("ai", "null", message);
                    String jsonString = JSONObject.toJSONString(sendMessage);
                    session.getBasicRemote().sendText(jsonString);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
