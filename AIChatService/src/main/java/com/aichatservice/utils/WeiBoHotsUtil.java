package com.aichatservice.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * @author tengyue
 * @date 2025/6/29/16:03
 */
public class WeiBoHotsUtil {

    /**
     * 获取微博的热点信息列表
     * @return
     */
    public static List<String> GetWeiBoHots(){
        // 获取微博热点信息
        String apiUrl = "https://weibo.com/ajax/side/hotSearch";
        RestTemplate restTemplate = new RestTemplate();
        String json = restTemplate.getForObject(apiUrl, String.class);
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode jsonNode = objectMapper.readTree(json);
            String hots = jsonNode.get("logs").get("ext").toString();
            List<String> hotList = List.of(hots.split("words:")[1].split(","));
            return hotList;
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
