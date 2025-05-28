/**
 * 文件描述
 *
 * @ProductName: AI Sample
 * @ProjectName: simple-chat
 * @Package: com.wxh.ai.controller
 * @Description: note
 * @Author: wangxh
 * @CreateDate: 2025/4/8 19:42
 * @UpdateUser: wangxh
 * @UpdateDate: 2025/4/8 19:42
 * @UpdateRemark: The modified content
 * @Version: 1.0
 * <p>
 * Copyright © 2025 wxh Technologies Inc. All Rights Reserved
 **/
package com.wxh.ai.mcp.controller;

import com.wxh.ai.mcp.service.ChatService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

/**
 * desc
 * @author wangxh
 * @since 2025-04-08
 **/
@RestController
@RequestMapping("/mcp")
public class ChatController {

    @Autowired
    private ChatService chatService;

    @GetMapping("/chat")
    public String chat(String message, String model){
        return chatService.chat(message, model);
    }
}
