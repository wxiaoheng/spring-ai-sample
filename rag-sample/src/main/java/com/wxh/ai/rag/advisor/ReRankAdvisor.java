/**
 * 文件描述
 *
 * @ProductName: AI Sample
 * @ProjectName: spring-ai-sample
 * @Package: com.wxh.ai.rag.advisor
 * @Description: note
 * @Author: wangxh
 * @CreateDate: 2025/4/22 20:31
 * @UpdateUser: wangxh
 * @UpdateDate: 2025/4/22 20:31
 * @UpdateRemark: The modified content
 * @Version: 1.0
 * <p>
 * Copyright © 2025 wxh Technologies Inc. All Rights Reserved
 **/
package com.wxh.ai.rag.advisor;

import com.wxh.ai.rag.model.rerank.RerankResponse;
import com.wxh.ai.rag.model.rerank.RerankResponseBody;
import com.wxh.ai.rag.model.rerank.RerankRequest;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.*;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.*;
import java.util.stream.Collectors;

/**
 * desc
 * @author wangxh
 * @since 2025-04-22
 **/
public class ReRankAdvisor  implements BaseAdvisor{
    private final WebClient client;

    private static final PromptTemplate promptTemplate = new PromptTemplate("{query}\n\nContext information is below, surrounded by ---------------------\n\n---------------------\n{question_answer_context}\n---------------------\n\nGiven the context and provided history information and not prior knowledge,\nreply to the user comment. If the answer is not in the context, inform\nthe user that you can't answer the question.\n");
    public ReRankAdvisor(WebClient client){
        this.client = client;
    }

    private List<Document> reRank(String query, Map<String, Object> context) {
        List<Document> documents = (List<Document>)context.get("qa_retrieved_documents");
        List<Document> reranks = new ArrayList<>();
        List<String> docs = documents.stream().map(e->e.getText()).collect(Collectors.toList());
        RerankRequest body = RerankRequest.builder().request_id(UUID.randomUUID().toString()).query(query).top_n(5)
            .return_documents(false).return_raw_scores(true).documents(docs).build();
        RerankResponse response = client.post().bodyValue(body).retrieve().bodyToMono(RerankResponse.class).block();
        for (RerankResponseBody result : response.getResults()){
            reranks.add(documents.get(result.getIndex()));
        }
        return reranks;
    }

    @Override
    public String getName() {
        return "reRank";
    }

    @Override
    public ChatClientRequest before(ChatClientRequest chatClientRequest, AdvisorChain advisorChain) {
        Map<String, Object> context = new HashMap(chatClientRequest.context());
        String origin = context.get("orgin_question").toString();
        List<Document> documents = reRank(origin, context);
        context.put("qa_retrieved_documents", documents);
        String documentContext = documents == null ? "" : documents.stream().map(Document::getText).collect(Collectors.joining(System.lineSeparator()));
        String augmentedUserText = this.promptTemplate.render(Map.of("query", origin, "question_answer_context", documentContext));
        return chatClientRequest.mutate().prompt(chatClientRequest.prompt().augmentUserMessage(augmentedUserText)).context(context).build();
    }

    @Override
    public ChatClientResponse after(ChatClientResponse chatClientResponse, AdvisorChain advisorChain) {
        return chatClientResponse;
    }

    @Override
    public int getOrder() {
        return 1;
    }
}
