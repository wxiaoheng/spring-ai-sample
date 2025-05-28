/**
 * 文件描述
 *
 * @ProductName: AI Sample
 * @ProjectName: spring-ai-sample
 * @Package: com.wxh.ai.rag.model.rerank
 * @Description: note
 * @Author: wangxh
 * @CreateDate: 2025/4/22 19:52
 * @UpdateUser: wangxh
 * @UpdateDate: 2025/4/22 19:52
 * @UpdateRemark: The modified content
 * @Version: 1.0
 * <p>
 * Copyright © 2025 wxh Technologies Inc. All Rights Reserved
 **/
package com.wxh.ai.rag.model.rerank;

import lombok.Builder;
import lombok.Data;

/**
 * desc
 * @author wangxh
 * @since 2025-04-22
 **/
@Data
@Builder
public class RerankResponseBody {
    private Integer index;

    private Float relevance_score;

    private String document;
}
