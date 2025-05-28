/**
 * 文件描述
 *
 * @ProductName: AI Sample
 * @ProjectName: simple-chat
 * @Package: com.wxh.ai.service
 * @Description: note
 * @Author: wangxh
 * @CreateDate: 2025/4/8 20:45
 * @UpdateUser: wangxh
 * @UpdateDate: 2025/4/8 20:45
 * @UpdateRemark: The modified content
 * @Version: 1.0
 * <p>
 * Copyright © 2025 wxh Technologies Inc. All Rights Reserved
 **/
package com.wxh.ai.service;

import reactor.core.publisher.Flux;

/**
 * desc
 * @author wangxh
 * @since 2025-04-08
 **/
public interface ChatService {
    Flux<String> streamChat(String message, String model);

    String chat(String message, String model);
}
