/**
 * 文件描述
 *
 * @ProductName: AI Sample
 * @ProjectName: spring-ai-sample
 * @Package: com.wxh.ai.rag.model
 * @Description: note
 * @Author: wangxh
 * @CreateDate: 2025/4/17 20:49
 * @UpdateUser: wangxh
 * @UpdateDate: 2025/4/17 20:49
 * @UpdateRemark: The modified content
 * @Version: 1.0
 * <p>
 * Copyright © 2025 wxh Technologies Inc. All Rights Reserved
 **/
package com.wxh.ai.rag.model;

import lombok.Data;

/**
 * desc
 * @author wangxh
 * @since 2025-04-17
 **/
@Data
public class IKnownResponse {
    int code;
    String msg;

    FaqDetailList data;

}
