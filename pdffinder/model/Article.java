package com.example.pdffinder.model;

public class Article {
    private String fileName;
    private String textBody;


    public Article() {}
    public Article(String fileName, String textBody) {
        this.fileName = fileName;
        this.textBody = textBody;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getTextBody() {
        return textBody;
    }

    public void setTextBody(String textBody) {
        this.textBody = textBody;
    }
}
