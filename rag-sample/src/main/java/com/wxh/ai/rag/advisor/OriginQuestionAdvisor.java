/**
 * 文件描述
 *
 * @ProductName: AI Sample
 * @ProjectName: spring-ai-sample
 * @Package: com.wxh.ai.rag.advisor
 * @Description: note
 * @Author: wangxh
 * @CreateDate: 2025/4/22 20:13
 * @UpdateUser: wangxh
 * @UpdateDate: 2025/4/22 20:13
 * @UpdateRemark: The modified content
 * @Version: 1.0
 * <p>
 * Copyright © 2025 wxh Technologies Inc. All Rights Reserved
 **/
package com.wxh.ai.rag.advisor;

import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.AdvisorChain;
import org.springframework.ai.chat.client.advisor.api.BaseAdvisor;
import org.springframework.core.Ordered;

import java.util.HashMap;
import java.util.Map;

/**
 * 将原始问题在Advisor上下文中保存
 * @author wangxh
 * @since 2025-04-22
 **/
public class OriginQuestionAdvisor implements BaseAdvisor {


    @Override
    public String getName() {
        return "记录原始请求";
    }

    @Override
    public ChatClientRequest before(ChatClientRequest request, AdvisorChain advisorChain) {
        String question = request.prompt().getContents();
        Map<String, Object> advisedUserParams = new HashMap(request.context());
        advisedUserParams.put("orgin_question", question);
        ChatClientRequest clientRequest = ChatClientRequest.builder()
            .context(advisedUserParams).prompt(request.prompt().copy())
            .build();
        return clientRequest;
    }

    @Override
    public ChatClientResponse after(ChatClientResponse chatClientResponse, AdvisorChain advisorChain) {
        return chatClientResponse;
    }


    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
