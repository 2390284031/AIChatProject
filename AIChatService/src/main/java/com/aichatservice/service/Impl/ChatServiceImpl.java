package com.aichatservice.service.Impl;

import com.aichatservice.service.ChatService;
import com.aichatservice.utils.DeepSeekChatUtil;
import com.aichatservice.utils.DeepSeekProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author tengyue
 * @date 2025/6/28/15:22
 */
@Service
public class ChatServiceImpl implements ChatService {

    @Autowired
    private DeepSeekProperties deepSeekProperties;

    @Override
    public void aiChat(String message) {
        DeepSeekChatUtil.streamChatCompletion(deepSeekProperties.getMODEL(), deepSeekProperties.getAPI_URL(),
                deepSeekProperties.getAPI_KEY(), message);
    }
}
