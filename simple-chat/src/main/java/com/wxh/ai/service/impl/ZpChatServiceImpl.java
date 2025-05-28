/**
 * 文件描述
 *
 * @ProductName: AI Sample
 * @ProjectName: simple-chat
 * @Package: com.wxh.ai.service.impl
 * @Description: note
 * @Author: wangxh
 * @CreateDate: 2025/4/8 19:11
 * @UpdateUser: wangxh
 * @UpdateDate: 2025/4/8 19:11
 * @UpdateRemark: The modified content
 * @Version: 1.0
 * <p>
 * Copyright © 2025 wxh Technologies Inc. All Rights Reserved
 **/
package com.wxh.ai.service.impl;

import com.wxh.ai.service.ChatService;
import com.wxh.ai.tools.DateTimeTool;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.zhipuai.ZhiPuAiChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.UUID;

/**
 * desc
 * @author wangxh
 * @since 2025-04-08
 **/
@Service("zpChat")
public class ZpChatServiceImpl implements ChatService {
    private ChatClient chatClient;
    private String conversationId = UUID.randomUUID().toString();

    @Autowired
    public ZpChatServiceImpl( ZhiPuAiChatModel chatModel) {
        MessageWindowChatMemory chatMemory = MessageWindowChatMemory.builder().maxMessages(10)
            .chatMemoryRepository(new InMemoryChatMemoryRepository())
            .build();
        this.chatClient = ChatClient.builder(chatModel)
            // 支持会话记忆
            .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
            // 添加日期时间获取工具以支持function call
            .defaultTools(new DateTimeTool())
            .build();
    }
    @Override
    public Flux<String> streamChat(String message, String model) {
        return chatClient.prompt(message)
            .advisors(advisor -> advisor
                .param(ChatMemory.CONVERSATION_ID, conversationId))
            .stream().content();
    }

    @Override
    public String chat(String message, String model) {
        ChatResponse response = chatClient.prompt(message).advisors(
                advisor -> advisor.param(ChatMemory.CONVERSATION_ID, conversationId))
            .call().chatResponse();
        return response.getResult().getOutput().getText();
    }
}
