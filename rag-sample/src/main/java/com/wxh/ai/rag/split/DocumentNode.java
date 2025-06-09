package com.wxh.ai.rag.split;

import java.util.ArrayList;
import java.util.List;

/**
 * desc
 * @author wangxh
 * @since 2025-06-09
 **/
public class DocumentNode {
    private int level;
    private String content = "";
    private List<DocumentNode> children;

    public DocumentNode(int level, String content) {
        this.level = level;
        this.content = content;
        this.children = new ArrayList<>();
    }

    public void addChild(DocumentNode child) {
        this.children.add(child);
    }

    // Getters and Setters
    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<DocumentNode> getChildren() {
        return children;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < level; i++) {
            sb.append("\t");
        }
        sb.append("Level: ").append(level).append(", Content: '").append(content).append("'\n");
        for (DocumentNode child : children) {
            sb.append(child.toString());
        }
        return sb.toString();
    }
}
