package com.wxh.ai;

import com.wxh.ai.service.TestService;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * desc
 *
 * @author wangxh
 * @since 2025-05-22
 **/
@SpringBootApplication
public class McpServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(McpServerApplication.class, args);
    }


    @Bean
    public ToolCallbackProvider allTools(TestService service) {
        return MethodToolCallbackProvider.builder().toolObjects(service).build();
    }
}