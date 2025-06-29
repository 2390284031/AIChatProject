package com.aichatservice.controller;

import com.aichatservice.pojo.Result;
import com.aichatservice.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author tengyue
 * @date 2025/6/27/21:43
 */
@RestController
@RequestMapping(("/chat"))
public class ChatController {

    @Autowired
    private ChatService chatService;

    @GetMapping("/getHots")
    public Result<List<String>> getHots(){
        return Result.success(chatService.getHots());
    }
}
