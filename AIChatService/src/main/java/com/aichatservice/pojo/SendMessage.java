package com.aichatservice.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author tengyue
 * @date 2025/6/28/18:50
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SendMessage {
    // 发送信息的角色
    private String role;
    // 发送人邮箱
    private String sendEmail;
    // 发送信息内容
    private String content;
}
