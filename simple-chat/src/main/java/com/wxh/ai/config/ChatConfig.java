/**
 * 文件描述
 *
 * @ProductName: AI Sample
 * @ProjectName: simple-chat
 * @Package: com.wxh.ai.config
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
package com.wxh.ai.config;

import com.wxh.ai.tools.DateTimeTool;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.InMemoryChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.model.tool.ToolCallingManager;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.retry.RetryUtils;
import org.springframework.ai.support.ToolCallbacks;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.resolution.StaticToolCallbackResolver;
import org.springframework.ai.zhipuai.ZhiPuAiImageModel;
import org.springframework.ai.zhipuai.ZhiPuAiImageOptions;
import org.springframework.ai.zhipuai.api.ZhiPuAiImageApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

/**
 * desc
 * @author wangxh
 * @since 2025-04-08
 **/
@Configuration
public class ChatConfig {

    @Value("${ZP_API_KEY}")
    private String ZP_API_KEY;

    @Bean("AITools")
    public ToolCallback[] toolCallback(){
        return ToolCallbacks.from(new DateTimeTool());
    }

    @Bean("dsChatClient")
    public ChatClient dsChatClient(OpenAiChatModel chatModel){
        MessageWindowChatMemory chatMemory = MessageWindowChatMemory.builder().maxMessages(10)
            .chatMemoryRepository(new InMemoryChatMemoryRepository())
            .build();
        return ChatClient.builder(chatModel).defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build()).build();
    }

    @Bean("ollamaChatClient")
    public ChatClient ollamaChatClient(){
        MessageWindowChatMemory chatMemory = MessageWindowChatMemory.builder().maxMessages(10)
            .chatMemoryRepository(new InMemoryChatMemoryRepository())
            .build();
        OllamaOptions option = OllamaOptions.builder().model("deepseek-r1:1.5b").build();
        OllamaChatModel model = OllamaChatModel.builder().ollamaApi(OllamaApi.builder().build()).defaultOptions(option)
            .toolCallingManager(ToolCallingManager.builder()
                .toolCallbackResolver(new StaticToolCallbackResolver(Arrays.asList(ToolCallbacks.from(new DateTimeTool()))))
                .build())
            .build();
        return ChatClient.builder(model).defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build()).build();
    }

    @Bean("zhipuAiImageModel")
    public ZhiPuAiImageModel zhiPuAiImageModel(){
        ZhiPuAiImageApi zhiPuAiImageApi = new ZhiPuAiImageApi(ZP_API_KEY);
        ZhiPuAiImageModel imageModel = new ZhiPuAiImageModel(zhiPuAiImageApi,
            ZhiPuAiImageOptions.builder().model("cogview-4-250304").build(),
            RetryUtils.DEFAULT_RETRY_TEMPLATE);
        return imageModel;
    }
}
