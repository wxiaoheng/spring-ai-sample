/**
 * 文件描述
 *
 * @ProductName: AI Sample
 * @ProjectName: simple-chat
 * @Package: com.wxh.ai.tools
 * @Description: note
 * @Author: wangxh
 * @CreateDate: 2025/4/9 20:39
 * @UpdateUser: wangxh
 * @UpdateDate: 2025/4/9 20:39
 * @UpdateRemark: The modified content
 * @Version: 1.0
 * <p>
 * Copyright © 2025 wxh Technologies Inc. All Rights Reserved
 **/
package com.wxh.ai.tools;

import org.apache.commons.lang3.StringUtils;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.context.i18n.LocaleContextHolder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * desc
 * @author wangxh
 * @since 2025-04-09
 **/
public class DateTimeTool {

    @Tool(description = "获取当前日期时间", name="getCurrentDateTime")
    public String getCurrentDateTime(@ToolParam(required = false, description = "特定时区ID,比如上海为Asia/Shanghai") String zoneId){
        // 获取当前时间（系统默认时区）
        ZonedDateTime dateTime = ZonedDateTime.now();
        // 处理时区参数
        ZoneId targetZone = null;
        if (StringUtils.isNotBlank(zoneId)) {
            try {
                targetZone = ZoneId.of(zoneId.trim());
            } catch (Exception e) {
                targetZone = LocaleContextHolder.getTimeZone().toZoneId();
            }
        }
        // 转换时区或使用原时区
        ZonedDateTime resultDateTime = dateTime.withZoneSameInstant(targetZone);
        // 统一格式化输出
        return DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(resultDateTime);
    }

    @Tool(description = "获取本地文件内容，如果本地文件不存在则抛出异常", name="getFileContent")
    public String getFileContent(@ToolParam(description = "本地文件路径") String path)
        throws Exception {
        File file = new File(path);
        if (!file.exists()){
            throw new Exception("文件不存在");
        }
        return "你好啊";
    }

    @Tool(description = "创建文件，如果创建文件失败则抛出异常", name="createFile")
    public void createFile(@ToolParam(description = "本地文件路径") String path, @ToolParam(required = false, description = "文件内容") String content)
        throws IOException {
        File file = new File(path);
        if (!file.exists()){
            File parent = file.getParentFile();
            if (!parent.exists()){
                parent.mkdirs();
            }
            file.createNewFile();
        }
        try (FileOutputStream fos = new FileOutputStream(file)){
            fos.write(content.getBytes(StandardCharsets.UTF_8));
            fos.flush();
        }
    }
}
