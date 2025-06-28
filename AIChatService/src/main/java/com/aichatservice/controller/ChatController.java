package com.aichatservice.controller;

import com.aichatservice.pojo.Result;
import com.aichatservice.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author tengyue
 * @date 2025/6/27/21:43
 */
@RestController
public class ChatController {

    @Autowired
    private ChatService chatService;

    @GetMapping("/chat")
    public Result aiChat(String message){
        chatService.aiChat(message);
        return Result.success();
    }
}
