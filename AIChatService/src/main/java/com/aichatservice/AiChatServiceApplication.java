package com.aichatservice;

import com.aichatservice.websocket.WebSocketServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@Slf4j
@SpringBootApplication
public class AiChatServiceApplication {

    public static void main(String[] args) {
        SpringApplication sa = new SpringApplication(AiChatServiceApplication.class);
        ConfigurableApplicationContext context = sa.run(args);
        WebSocketServer.setApplicationContext(context);
        log.info("项目启动成功");
    }

}
