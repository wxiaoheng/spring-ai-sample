/**
 * 文件描述
 *
 * @ProductName: AI Sample
 * @ProjectName: spring-ai-sample
 * @Package: com.wxh.ai.rag.model
 * @Description: note
 * @Author: wangxh
 * @CreateDate: 2025/4/17 19:54
 * @UpdateUser: wangxh
 * @UpdateDate: 2025/4/17 19:54
 * @UpdateRemark: The modified content
 * @Version: 1.0
 * <p>
 * Copyright © 2025 wxh Technologies Inc. All Rights Reserved
 **/
package com.wxh.ai.rag.model;

import lombok.Data;
import org.jsoup.Jsoup;

/**
 * desc
 * @author wangxh
 * @since 2025-04-17
 **/
@Data
public class FaqDocument {
    private String question;
    private String answer;

    public void setQuestion(String question) {
        this.question = Jsoup.parse(question).text();
    }

    public void setAnswer(String answer) {
        this.answer = Jsoup.parse(answer).text();
    }

}
