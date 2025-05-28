/**
 * 文件描述
 *
 * @ProductName: AI Sample
 * @ProjectName: simple-chat
 * @Package: com.wxh.ai.service.impl
 * @Description: note
 * @Author: wangxh
 * @CreateDate: 2025/4/8 19:51
 * @UpdateUser: wangxh
 * @UpdateDate: 2025/4/8 19:51
 * @UpdateRemark: The modified content
 * @Version: 1.0
 * <p>
 * Copyright © 2025 wxh Technologies Inc. All Rights Reserved
 **/
package com.wxh.ai.service.impl;

import com.wxh.ai.service.ImageService;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.image.ImageResponse;
import org.springframework.ai.zhipuai.ZhiPuAiImageModel;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * desc
 * @author wangxh
 * @since 2025-04-08
 **/
@Service
public class ZpImageServiceImpl implements ImageService {

    private ZhiPuAiImageModel imageModel;
    public ZpImageServiceImpl(@Qualifier("zhipuAiImageModel")ZhiPuAiImageModel imageModel){
        this.imageModel = imageModel;
    }
    @Override
    public String generateImage(String message) {
        ImageResponse response = imageModel.call(new ImagePrompt(message));
        return response.getResult().getOutput().getUrl();
    }
}
