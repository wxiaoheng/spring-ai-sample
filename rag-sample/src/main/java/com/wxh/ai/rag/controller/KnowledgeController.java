/**
 * 文件描述
 *
 * @ProductName: AI Sample
 * @ProjectName: spring-ai-sample
 * @Package: com.wxh.ai.rag.controller
 * @Description: note
 * @Author: wangxh
 * @CreateDate: 2025/4/17 20:12
 * @UpdateUser: wangxh
 * @UpdateDate: 2025/4/17 20:12
 * @UpdateRemark: The modified content
 * @Version: 1.0
 * <p>
 * Copyright © 2025 wxh Technologies Inc. All Rights Reserved
 **/
package com.wxh.ai.rag.controller;

import com.wxh.ai.rag.service.KnowledgeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * desc
 * @author wangxh
 * @since 2025-04-17
 **/
@RestController
public class KnowledgeController {

    @Autowired
    private KnowledgeService knowledgeService;

    @PostMapping("init")
    public void build(){
        knowledgeService.build();
    }
}
