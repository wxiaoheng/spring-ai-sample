/**
 * 文件描述
 *
 * @ProductName: Light Code
 * @ProjectName: spring-ai-sample
 * @Package: com.wxh.ai.rag.split
 * @Description: note
 * @Author: wangxh
 * @CreateDate: 2025/6/9 16:10
 * @UpdateUser: wangxh
 * @UpdateDate: 2025/6/9 16:10
 * @UpdateRemark: The modified content
 * @Version: 1.0
 * <p>
 * Copyright © 2025 Hundsun Technologies Inc. All Rights Reserved
 **/
package com.wxh.ai.rag.split;

import org.apache.poi.xwpf.usermodel.*;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPPrGeneral;
import org.springframework.ai.document.Document;

import javax.print.Doc;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.StringJoiner;

/**
 * desc
 * @author wangxh
 * @since 2025-06-09
 **/
public class WordParser {

    private List<Document> docs = new ArrayList<>();
    private boolean levelFind = false;
    private StringBuilder builder = new StringBuilder();

    public List<DocumentNode> parse(String filePath) throws IOException {
        List<DocumentNode> rootNodes = new ArrayList<>();
        Stack<DocumentNode> nodeStack = new Stack<>();

        try (FileInputStream fis = new FileInputStream(filePath);
            XWPFDocument document = new XWPFDocument(fis)) { // document 对象现在需要在整个方法中可用

            for (IBodyElement element : document.getBodyElements()) {
                if (element instanceof XWPFParagraph) {
                    XWPFParagraph paragraph = (XWPFParagraph) element;
                    // *** 方法签名已更改，现在需要传入 document ***
                    int level = getOutlineLevel(paragraph, document);
                    String text = paragraph.getText();

                    if (text == null || text.trim().isEmpty()) {
                        continue;
                    }

                    DocumentNode newNode = new DocumentNode(level, text);

                    // 构建层级关系的逻辑保持不变
                    if (level > 0) {
                        while (!nodeStack.isEmpty() && nodeStack.peek().getLevel() >= level) {
                            nodeStack.pop();
                        }
                        if (!nodeStack.isEmpty()) {
                            nodeStack.peek().addChild(newNode);
                        } else {
                            rootNodes.add(newNode);
                        }
                        nodeStack.push(newNode);
                    } else {
                        if (!nodeStack.isEmpty()) {
                            nodeStack.peek().addChild(newNode);
                        } else {
                            rootNodes.add(newNode);
                        }
                    }
                } else if (element instanceof XWPFTable) {
                    XWPFTable table = (XWPFTable) element;
                    String markdownTable = convertTableToMarkdown(table);
                    DocumentNode tableNode = new DocumentNode(0, markdownTable);

                    if (!nodeStack.isEmpty()) {
                        nodeStack.peek().addChild(tableNode);
                    } else {
                        rootNodes.add(tableNode);
                    }
                }
            }
        }
        return rootNodes;
    }

    /**
     * 使用混合策略获取段落的真实大纲级别。
     * @param paragraph 要检查的段落。
     * @param document 文档对象，用于访问样式库。
     * @return 大纲级别 (1-9)，或 0 表示普通文本。
     */
    private int getOutlineLevel(XWPFParagraph paragraph, XWPFDocument document) {
        // 策略 1: 检查段落自身的直接属性
        CTPPr ppr = paragraph.getCTP().getPPr();
        if (ppr != null && ppr.getOutlineLvl() != null) {
            return ppr.getOutlineLvl().getVal().intValue() + 1;
        }

        // 策略 2: 检查段落应用的样式的属性 (最关键的一步)
        String styleId = paragraph.getStyleID();
        if (styleId != null) {
            XWPFStyle style = document.getStyles().getStyle(styleId);
            if (style != null) {
                // 从样式的段落属性中获取大纲级别
                CTPPrGeneral stylePPr =
                    style.getCTStyle().getPPr();
                if (stylePPr != null && stylePPr.getOutlineLvl() != null) {
                    return stylePPr.getOutlineLvl().getVal().intValue() + 1;
                }

                // 策略 3: 启发式备用方案，通过样式名称判断
                // 这对于某些不规范的文档很有效
                String styleName = style.getName();
                if (styleName != null) {
                    String lowerStyleName = styleName.toLowerCase();
                    // 兼容英文和中文的默认标题样式
                    if (lowerStyleName.startsWith("heading ") || lowerStyleName.startsWith("标题 ")) {
                        try {
                            String levelStr = lowerStyleName.replaceAll("[^0-9]", "");
                            if (!levelStr.isEmpty()) {
                                return Integer.parseInt(levelStr);
                            }
                        } catch (NumberFormatException e) {
                            // 忽略解析失败
                        }
                    }
                }
            }
        }

        // 如果都找不到，则为普通文本
        return 0;
    }

    // convertTableToMarkdown 方法保持不变
    private String convertTableToMarkdown(XWPFTable table) {
        StringBuilder markdown = new StringBuilder();
        List<XWPFTableRow> rows = table.getRows();
        if (rows.isEmpty()) {
            return "";
        }
        XWPFTableRow headerRow = rows.get(0);
        for (XWPFTableCell cell : headerRow.getTableCells()) {
            markdown.append("| ").append(cell.getText().trim()).append(" ");
        }
        markdown.append("|\n");
        for (int i = 0; i < headerRow.getTableCells().size(); i++) {
            markdown.append("|---");
        }
        markdown.append("|\n");
        for (int i = 1; i < rows.size(); i++) {
            XWPFTableRow dataRow = rows.get(i);
            for (XWPFTableCell cell : dataRow.getTableCells()) {
                markdown.append("| ").append(cell.getText().trim()).append(" ");
            }
            markdown.append("|\n");
        }
        return markdown.toString();
    }

    public List<Document> read(String path, int splitLevel) throws IOException {
        List<DocumentNode> nodes = parse(path);
        docs.clear();
        levelFind = false;
        builder.setLength(0);
        buildDocs(nodes, splitLevel);
        if (builder.length() >0){
            Document doc = new Document(builder.toString());
            docs.add(doc);
            builder.setLength(0);
            levelFind = false;
        }
        return docs;
    }


    private void buildDocs(List<DocumentNode> nodes, int splitLevel){
        for (DocumentNode node : nodes) {
            if (node.getLevel()< splitLevel || node.getLevel() == 0){
                levelFind = true;
                builder.append(node.getContent().trim());
                builder.append("\n");
            }else {
                if (levelFind){
                    Document doc = new Document(builder.toString());
                    docs.add(doc);
                    builder.setLength(0);
                    levelFind = false;
                }
                builder.append(node.getContent().trim());
                builder.append("\n");
            }
            buildDocs(node.getChildren(), splitLevel);
        }
    }
}
