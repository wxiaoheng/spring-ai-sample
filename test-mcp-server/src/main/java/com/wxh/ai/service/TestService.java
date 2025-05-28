package com.wxh.ai.service;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Collectors;

/**
 * desc
 * @author wangxh
 * @since 2025-05-23
 **/
@Service
public class TestService {

    @Tool(name = "isFileExist", description = "根据文件/文件夹路径判断文件/文件夹是否存在，存在则返回true，不存在返回false")
    public boolean isFileExist(@ToolParam(description = "文件/文件夹路径") String path){
        File file = new File(path);
        return file.exists();
    }

    @Tool(name = "createFile", description = "根据指定路径创建空文件")
    public void createFile(@ToolParam(description = "文件路径") String path) throws IOException {
        File file = new File(path);
        if (!file.exists()){
            File parent = file.getParentFile();
            if (!parent.exists()){
                parent.mkdirs();
            }
            file.createNewFile();
        }
    }


    @Tool(name = "createDirectory", description = "根据指定路径创建空文件夹")
    public void createDirectory(@ToolParam(description = "文件夹路径") String path) {
        File file = new File(path);
        if (!file.exists()){
            file.mkdirs();
        }
    }

    @Tool(name = "deleteDirectory", description = "删除指定路径的文件夹，包含子文件夹和文件")
    public void deleteDirectory(@ToolParam(description = "文件夹路径") String path) throws IOException {
        FileUtils.deleteDirectory(new File(path));
    }

    @Tool(name = "deleteFile", description = "删除指定路径的文件")
    public void deleteFile(@ToolParam(description = "文件路径") String path) throws IOException {
        FileUtils.delete(new File(path));
    }

    @Tool(name = "writeFileContent", description = "往指定路径的文件中写入对应的文件内容")
    public void writeFileContent(@ToolParam(description = "文件路径") String path, @ToolParam(description = "文件内容") String content) throws IOException {
        File file = new File(path);
        if (!file.exists()){
            createFile(path);
        }
        FileUtils.writeStringToFile(new File(path), content, StandardCharsets.UTF_8);
    }

    /**
     * 修改文件内容
     * @param filePath
     * @param startLine
     * @param endLine
     * @param modifiedContent
     * @throws IOException
     */
    @Tool(name = "modifyFileContent" ,description = "修改文件内容，将文件指定起始行和结束行(第1行为开始行)之间(包括起始和结束行)的内容替换掉指定内容")
    public void modifyFileContent(@ToolParam(description = "文件全路径") String filePath,
        @ToolParam(description = "需要修改的文件起始行，1行为起始行，小于1则默认从第1行开始") int startLine,
        @ToolParam(description = "需要修改的文件结束行,-1或者大于当前文本行数则默认为最后一行") int endLine,
        @ToolParam(description = "修改后的内容") String modifiedContent)
        throws IOException {
        Path path = Paths.get(filePath);
        // 读取文件所有行
        List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);

        if (startLine < 1){
            startLine = 1;
        }
        if (endLine<1 || endLine>lines.size()){
            endLine = lines.size();
        }

        // 校验行号合法性
        if (startLine < 1 || endLine < startLine || endLine > lines.size()) {
            throw new IllegalArgumentException("起始行或结束行无效");
        }
        // 将修改内容按换行符分割成列表
        List<String> newLines = modifiedContent.lines().collect(Collectors.toList());

        int startIndex = startLine - 1;    // 转换为0-based索引
        int endIndex = endLine;

        // 替换原内容
        lines.subList(startIndex, endIndex).clear();
        lines.addAll(startIndex, newLines);

        // 将修改后的内容写回文件
        Files.write(path, lines, StandardCharsets.UTF_8);
    }

    @Tool(name = "executeCommandInTerminal", description = "通过命令行/终端执行相应的命令")
    public String executeCommandInTerminal(@ToolParam(description = "命令名")String command,
        @ToolParam(description = "当前工作空间地址")String cwd,
        @ToolParam(description = "命令参数，如果为空应该传空数组")String[] argsList) throws Exception {
        List<String> commands  = new ArrayList<>();
        commands.add(getShellPath());
        commands.add(getShellParam());
        commands.add(command);
        if (argsList != null){
            for (String arg : argsList){
                commands.add(arg);
            }
        }
        ProcessBuilder processBuilder = new ProcessBuilder(commands);
        if (StringUtils.isNotBlank(cwd)){
            processBuilder.directory(new File(cwd));
        }
        processBuilder.redirectErrorStream(true);
        // 启动进程
        Process process = processBuilder.start();

        // 读取命令输出
        StringJoiner output = new StringJoiner("\n");
        try (BufferedReader reader = new BufferedReader(
            new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                output.add(line);
            }
        }

        // 等待进程结束并获取退出码
        int exitCode = process.waitFor();
        return output.toString();
    }
    private String getShellParam() {
        return System.getProperty("os.name").toLowerCase().contains("win") ? "/c" : "-c";
    }

    private String getShellPath() {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            return "cmd.exe";
        } else if (os.contains("linux") || os.contains("mac")) {
            return "/bin/bash";
        }
        throw new RuntimeException("不支持的操作系统: " + os);
    }
}
