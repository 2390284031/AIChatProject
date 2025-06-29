package com.aichatservice.service.Impl;

import com.aichatservice.service.ChatService;
import com.aichatservice.utils.WeiBoHotsUtil;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author tengyue
 * @date 2025/6/28/15:22
 */
@Service
public class ChatServiceImpl implements ChatService {


    @Override
    public List<String> getHots() {
        return WeiBoHotsUtil.GetWeiBoHots();
    }
}
