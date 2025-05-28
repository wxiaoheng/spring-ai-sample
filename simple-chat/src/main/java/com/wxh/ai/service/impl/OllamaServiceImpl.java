/**
 * 文件描述
 *
 * @ProductName: AI Sample
 * @ProjectName: simple-chat
 * @Package: com.wxh.ai.service.impl
 * @Description: note
 * @Author: wangxh
 * @CreateDate: 2025/4/8 19:08
 * @UpdateUser: wangxh
 * @UpdateDate: 2025/4/8 19:08
 * @UpdateRemark: The modified content
 * @Version: 1.0
 * <p>
 * Copyright © 2025 wxh Technologies Inc. All Rights Reserved
 **/
package com.wxh.ai.service.impl;

import com.wxh.ai.service.ChatService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.UUID;

/**
 * desc
 * @author wangxh
 * @since 2025-04-08
 **/
@Service("ollamaChat")
public class OllamaServiceImpl implements ChatService {
    private ChatClient chatClient;

    private String conversationId = UUID.randomUUID().toString();

    @Autowired
    @Qualifier("AITools")
    private ToolCallback[] toolCallbacks;

    @Autowired
    public OllamaServiceImpl(@Qualifier("ollamaChatClient") ChatClient chatClient) {
        this.chatClient = chatClient;
    }
    @Override
    public Flux<String> streamChat(String message, String model) {
//        conversationId = UUID.randomUUID().toString();
        ChatClient.StreamResponseSpec response = chatClient.prompt(message)
            //            .messages(new UserMessage("昨天周三，今天周几？"), new AssistantMessage("昨天周三的话，今天周四"),new UserMessage(message))
            .advisors(spec -> spec.param(ChatMemory.CONVERSATION_ID, conversationId)).stream();
        return response.content();
    }

    @Override
    public String chat(String message, String model) {
        ChatClient.CallResponseSpec response = chatClient.prompt(message)
            .advisors(spec -> spec.param(ChatMemory.CONVERSATION_ID, conversationId)).call();
        return response.content();
    }
}
