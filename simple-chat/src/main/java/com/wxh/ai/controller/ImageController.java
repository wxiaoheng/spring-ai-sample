/**
 * 文件描述
 *
 * @ProductName: AI Sample
 * @ProjectName: simple-chat
 * @Package: com.wxh.ai.controller
 * @Description: note
 * @Author: wangxh
 * @CreateDate: 2025/4/8 19:50
 * @UpdateUser: wangxh
 * @UpdateDate: 2025/4/8 19:50
 * @UpdateRemark: The modified content
 * @Version: 1.0
 * <p>
 * Copyright © 2025 wxh Technologies Inc. All Rights Reserved
 **/
package com.wxh.ai.controller;

import com.wxh.ai.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * desc
 * @author wangxh
 * @since 2025-04-08
 **/
@RestController
public class ImageController {

    @Autowired
    private ImageService imageService;

    @GetMapping("image")
    public String generateImage(String message){
        return imageService.generateImage(message);
    }
}
