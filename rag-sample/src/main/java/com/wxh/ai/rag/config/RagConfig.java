/**
 * 文件描述
 *
 * @ProductName: AI Sample
 * @ProjectName: spring-ai-sample
 * @Package: com.wxh.ai.rag.config
 * @Description: note
 * @Author: wangxh
 * @CreateDate: 2025/4/17 20:48
 * @UpdateUser: wangxh
 * @UpdateDate: 2025/4/17 20:48
 * @UpdateRemark: The modified content
 * @Version: 1.0
 * <p>
 * Copyright © 2025 wxh Technologies Inc. All Rights Reserved
 **/
package com.wxh.ai.rag.config;

import io.qdrant.client.QdrantClient;
import io.qdrant.client.QdrantGrpcClient;
import org.springframework.ai.vectorstore.qdrant.QdrantVectorStore;
import org.springframework.ai.zhipuai.ZhiPuAiChatModel;
import org.springframework.ai.zhipuai.ZhiPuAiEmbeddingModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * desc
 * @author wangxh
 * @since 2025-04-17
 **/
@Configuration
public class RagConfig {

    @Autowired
    private ZhiPuAiEmbeddingModel embeddingModel;
    @Autowired
    ZhiPuAiChatModel chatModel;

    @Value("${ZP_API_KEY}")
    private String ZP_API_KEY;

//    @Bean
//    public ChromaVectorStore chromaVectorStore(){
//        return ChromaVectorStore.builder(new ChromaApi("http://localhost:8000"), embeddingModel).collectionName("jres").build();
//    }

    @Bean
    public QdrantVectorStore qdrantVectorStore(){
        QdrantGrpcClient grpcClient = QdrantGrpcClient.newBuilder("127.0.0.1", 6334, false).build();
        return QdrantVectorStore.builder(new QdrantClient(grpcClient), embeddingModel).collectionName("omc").build();
    }

    @Bean("rerankClient")
    public WebClient rerankClient(){
        return WebClient.builder().baseUrl("https://open.bigmodel.cn/api/paas/v4/rerank")
                    .defaultHeader(HttpHeaders.AUTHORIZATION, ZP_API_KEY)
                    .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE).build();
    }
}
