/**
 * 文件描述
 *
 * @ProductName: AI Sample
 * @ProjectName: simple-chat
 * @Package: com.wxh.ai.service.impl
 * @Description: note
 * @Author: wangxh
 * @CreateDate: 2025/4/8 20:11
 * @UpdateUser: wangxh
 * @UpdateDate: 2025/4/8 20:11
 * @UpdateRemark: The modified content
 * @Version: 1.0
 * <p>
 * Copyright © 2025 wxh Technologies Inc. All Rights Reserved
 **/
package com.wxh.ai.rag.service.impl;

import com.wxh.ai.rag.advisor.OriginQuestionAdvisor;
import com.wxh.ai.rag.advisor.ReRankAdvisor;
import com.wxh.ai.rag.service.ChatService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.qdrant.QdrantVectorStore;
import org.springframework.ai.zhipuai.ZhiPuAiChatModel;
import org.springframework.ai.zhipuai.ZhiPuAiEmbeddingModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.util.UUID;

/**
 * desc
 * @author wangxh
 * @since 2025-04-08
 **/
@Service
public class ZpChatServiceImpl implements ChatService {
    private ChatClient chatClient;
    private String conversationId = UUID.randomUUID().toString();

    @Autowired
    private ZhiPuAiEmbeddingModel embeddingModel;

    @Autowired
    public ZpChatServiceImpl( ZhiPuAiChatModel chatModel, QdrantVectorStore vectorStore, @Qualifier("rerankClient") WebClient client) {
        MessageWindowChatMemory chatMemory = MessageWindowChatMemory.builder().maxMessages(10)
            .chatMemoryRepository(new InMemoryChatMemoryRepository())
            .build();
        this.chatClient = ChatClient.builder(chatModel)
            // 支持会话记忆
            .defaultAdvisors(
                MessageChatMemoryAdvisor.builder(chatMemory).build(),
                // 支持从向量数据库中先进行检索再拼接prompt
                QuestionAnswerAdvisor.builder(vectorStore).searchRequest(SearchRequest.builder().topK(10).build()).build(),
                // 重排
                new ReRankAdvisor(client),
                new OriginQuestionAdvisor())
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
        return chatClient.prompt(message)
            .advisors(advisor -> advisor
                .param(ChatMemory.CONVERSATION_ID, conversationId))
            .call().content();
    }
}
