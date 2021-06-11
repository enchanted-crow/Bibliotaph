package com.example.pdffinder.model;

public class Article {
    private String source;
    private String fileName;

    public Article() {}

    public Article(String source, String fileName) {
        this.source = source;
        this.fileName = fileName;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
