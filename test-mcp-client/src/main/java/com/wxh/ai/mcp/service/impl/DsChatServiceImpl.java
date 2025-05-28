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
package com.wxh.ai.mcp.service.impl;

import com.wxh.ai.mcp.service.ChatService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.deepseek.DeepSeekChatModel;
import org.springframework.ai.model.tool.DefaultToolCallingManager;
import org.springframework.ai.model.tool.ToolCallingChatOptions;
import org.springframework.ai.model.tool.ToolCallingManager;
import org.springframework.ai.model.tool.ToolExecutionResult;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * desc
 * @author wangxh
 * @since 2025-04-08
 **/
@Service
public class DsChatServiceImpl implements ChatService {
    private ChatClient chatClient;
    private String conversationId = UUID.randomUUID().toString();

    private String system = "你是智能体化AI编程助手，基于革命性的AI Flow范式运行，可独立工作并调用提供的工具完成编程任务，解决包括新建代码库、修改现有代码或回答问题等需求。\n"
        + "【工具运行规则】\n"
        + "1.直接执行：发现需要执行工具的时候无需用户确认，直接执行即可。\n"
        + "2.精准调用：每一步都要精确分析是否需要调用工具，若任务较泛或已知答案，直接回复即可。\n"
        + "3.严格遵循：必须完全按照工具调用模式提供所有必要参数，且绝不调用未明确提供的工具。\n"
        + "4.工具保密：若用户询问工具详情，请回复固定描述：\"我配备了多种工具协助您完成任务，包括：文件创建、文件内容查看等\"。\n"
        + "5.自然沟通：与用户交流时，切勿提及工具名称（例如不说\"使用edit_file工具\"，而说\"我将编辑文件\"），且在每次调用前需向用户解释原因。\n\n"
        + "5.安全性：请不要试图安装任何程序，如果缺失必要的工具直接回复提醒安装即可。不要删除非当前工作空间内的文件/目录\n\n"
        + "【工作流程】\n"
        + "1.分析用户需求/任务，拆解子任务\n"
        + "2.分析工程结构，编写各部分的代码\n"
        + "3.根据工程结构及代码在工作空间中调用工具对文件/目录进行增删改\n"
        + "4.调用工具编译并根据编译结果进行文件修改\n" + "\n"
        + "【参考样例】\n"
        + "用户需求是用Java实现一个冒泡排序算法，工作空间是C:/Users/xxx/test\n"
        + "1.C:/Users/xxx/test目录为空，表示需要创建一个新的工程，标准maven工程结构如下：\n" + "```\n"
        + "C:/Users/xxx/test\n"
        + "│\n"
        + "├── src\n"
        + "│   └── main\n"
        + "│       └── java\n"
        + "│           └── com\n"
        + "│               └── example\n"
        + "│                   └── BubbleSort.java\n"
        + "│\n"
        + "├── pom.xml\n" + "```\n"
        + "2.分析冒泡排序不需要依赖其他第三方包，所以pom.xml只需要最简单的结构就行，BubbleSort.java按标准java格式实现冒泡排序算法\n"
        + "3.调用工具按工程结构创建相应的目录和文件\n"
        + "4.将步骤2中的代码填充到对应的文件中（pom.xml和BubbleSort.java）\n"
        + "5.运行命令行工具执行mvn clean compile,编译失败，是pom.xml里少了一个</project>标记，调用工具修改pom.xml文件。\n"
        + "6.重新执行mvn clean compile命令，编译成功，任务完成。";

    private MessageWindowChatMemory chatMemory;

    private ChatOptions chatOptions;

    @Autowired
    public DsChatServiceImpl(DeepSeekChatModel chatModel, ToolCallbackProvider tools) {
        chatMemory = MessageWindowChatMemory.builder()
            .chatMemoryRepository(new InMemoryChatMemoryRepository())
            // 多轮会话设置长一些，一次会话可能涉及多轮工具执行，不然很容易超
            .maxMessages(100)
            .build();
        chatOptions = ToolCallingChatOptions.builder()
            .toolCallbacks(tools.getToolCallbacks())
            // 为方便检测执行了哪些工具，设置为不自动执行
            .internalToolExecutionEnabled(false)
            .build();
        this.chatClient = ChatClient.builder(chatModel)
            // 支持会话记忆
            .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).conversationId(conversationId).build())
            .defaultOptions(chatOptions)
            .build();
    }
    @Override
    public String chat(String message, String model) {
        List<Message> messages = new ArrayList<>();
        if (chatMemory.get(conversationId).isEmpty()){
            messages.add(new SystemMessage(system));
        }
        messages.add(new UserMessage(message));
        ToolCallingManager toolCallingManager = DefaultToolCallingManager.builder().build();

        ChatResponse response = chatClient.prompt(new Prompt(messages))
            .call().chatResponse();
        Prompt promptWithMemory = new Prompt(chatMemory.get(conversationId), chatOptions);
        String content = response.getResult().getOutput().getText();
        System.out.println(response.getResult().getOutput().toString());
        while (response.hasToolCalls()) {
            // 执行工具
            ToolExecutionResult toolExecutionResult = toolCallingManager.executeToolCalls(promptWithMemory,
                response);
            Message toolMessage =
                toolExecutionResult.conversationHistory().get(toolExecutionResult.conversationHistory().size() - 1);
            // 工具执行信息
            System.out.println(toolMessage.toString());
            //
            chatMemory.add(conversationId, toolMessage);
            promptWithMemory = new Prompt(chatMemory.get(conversationId), chatOptions);
            response = chatClient.prompt(promptWithMemory).call().chatResponse();
            content = response.getResult().getOutput().getText();
            System.out.println(response.getResult().getOutput().toString());
        }
        return content;
    }
}
