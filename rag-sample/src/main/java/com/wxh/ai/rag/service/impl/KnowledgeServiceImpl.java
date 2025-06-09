/**
 * 文件描述
 *
 * @ProductName: AI Sample
 * @ProjectName: spring-ai-sample
 * @Package: com.wxh.ai.rag.service.impl
 * @Description: note
 * @Author: wangxh
 * @CreateDate: 2025/4/17 20:13
 * @UpdateUser: wangxh
 * @UpdateDate: 2025/4/17 20:13
 * @UpdateRemark: The modified content
 * @Version: 1.0
 * <p>
 * Copyright © 2025 wxh Technologies Inc. All Rights Reserved
 **/
package com.wxh.ai.rag.service.impl;

import com.wxh.ai.rag.service.KnowledgeService;
import com.wxh.ai.rag.split.WordParser;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TextSplitter;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.qdrant.QdrantVectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

/**
 * desc
 * @author wangxh
 * @since 2025-04-17
 **/
@Service
public class KnowledgeServiceImpl implements KnowledgeService {

    @Autowired
    private QdrantVectorStore vectorStore;

    /**
     * 本地知识文档
     */
    @Value("${app.resource}")
    private Resource word;

    @Override
    @Async
    public void build() {
        // word文档使用tika进行提取
        TikaDocumentReader documentReader = new TikaDocumentReader(word);
        List<Document> documents = documentReader.get();
        TextSplitter textSplitter = new TokenTextSplitter();
        // 使用默认tokenTextSplitter进行切分
        List<Document> splitDocuments = textSplitter.apply(documents);
        vectorStore.doAdd(splitDocuments);
        System.out.println("build suceess!");
//        try {
//            // 自己写提取切分方式
//            List<Document> splitDocuments = new WordParser().read(word.getFile().getAbsolutePath(), 3);
//            // 向量化并存储
//            vectorStore.doAdd(splitDocuments);
//            System.out.println("build suceess!");
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }

    }

}
