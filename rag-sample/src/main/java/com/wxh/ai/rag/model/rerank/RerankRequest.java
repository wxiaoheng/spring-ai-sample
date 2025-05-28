/**
 * 文件描述
 *
 * @ProductName: AI Sample
 * @ProjectName: spring-ai-sample
 * @Package: com.wxh.ai.rag.model
 * @Description: note
 * @Author: wangxh
 * @CreateDate: 2025/4/22 18:42
 * @UpdateUser: wangxh
 * @UpdateDate: 2025/4/22 18:42
 * @UpdateRemark: The modified content
 * @Version: 1.0
 * <p>
 * Copyright © 2025 wxh Technologies Inc. All Rights Reserved
 **/
package com.wxh.ai.rag.model.rerank;

import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * desc
 * @author wangxh
 * @since 2025-04-22
 **/
@Data
@Builder
public class RerankRequest {
    private String request_id;
    private String query;
    private Integer top_n;
    private List<String> documents = new ArrayList<>();
    private boolean return_documents;
    private boolean return_raw_scores;
}
