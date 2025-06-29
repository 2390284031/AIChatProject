package com.aichatservice.task;

import com.aichatservice.utils.WeiBoHotsUtil;
import com.aichatservice.websocket.WebSocketServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author tengyue
 * @date 2025/6/29/16:30
 */
@Component
@Slf4j
public class HotsTask {

    /**
     * 定时获取热点信息
     */
    @Scheduled(cron = "0 0 0/1 * * ?")//每1小时触发一次
    public void processGetHots(){
        List<String> hotList = WeiBoHotsUtil.GetWeiBoHots();
        log.info("定时获取热点话题：{}", hotList);
        WebSocketServer webSocketServer = new WebSocketServer();
        webSocketServer.sendToAllClient(hotList, null);
    }
}
