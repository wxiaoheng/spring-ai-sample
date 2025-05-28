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
package com.wxh.ai.controller;

import com.wxh.ai.service.ChatService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
    @Qualifier("dsChat")
    private ChatService dsChatService;

    @Autowired
    @Qualifier("ollamaChat")
    private ChatService ollamaChatService;

    @Autowired
    @Qualifier("zpChat")
    private ChatService zpChatService;

    @GetMapping("streamChat")
    public Flux<String> streamChat(String message, String model){
        if (StringUtils.isBlank(model) || StringUtils.equalsIgnoreCase(model, "deepseek")){
            return dsChatService.streamChat(message, model);
        }else if (StringUtils.equalsIgnoreCase(model, "zp")){
            return zpChatService.streamChat(message, model);
        }
        return ollamaChatService.streamChat(message, model);
    }


    @GetMapping("chat")
    public String chat(String message, String model){
        if (StringUtils.isBlank(model) || StringUtils.equalsIgnoreCase(model, "deepseek")){
            return dsChatService.chat(message, model);
        }else if (StringUtils.equalsIgnoreCase(model, "zp")){
            return zpChatService.chat(message, model);
        }
        return ollamaChatService.chat(message, model);
    }
}
