/**
 * 文件描述
 *
 * @ProductName: AI Sample
 * @ProjectName: simple-chat
 * @Package: com.wxh.ai.controller
 * @Description: note
 * @Author: wangxh
 * @CreateDate: 2025/4/8 20:42
 * @UpdateUser: wangxh
 * @UpdateDate: 2025/4/8 20:42
 * @UpdateRemark: The modified content
 * @Version: 1.0
 * <p>
 * Copyright © 2025 wxh Technologies Inc. All Rights Reserved
 **/
package com.wxh.ai.rag.controller;

import com.wxh.ai.rag.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

/**
 * desc
 * @author wangxh
 * @since 2025-04-08
 **/
@RestController
public class ChatController {

    @Autowired
    private ChatService zpChatService;

    @GetMapping("streamChat")
    public Flux<String> streamChat(String message, String model){
        return zpChatService.streamChat(message, model);
    }


    @GetMapping("chat")
    public String chat(String message, String model){
        return zpChatService.chat(message, model);
    }
}
