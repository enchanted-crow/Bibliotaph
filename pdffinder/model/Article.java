package com.example.pdffinder.model;

public class Article {
    private String fileName;
    private String textBody;
    private String dateAdded;

    public Article() {}

    public Article(String fileName, String textBody, String dateAdded) {
        this.fileName = fileName;
        this.textBody = textBody;
        this.dateAdded = dateAdded;
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

    public String getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(String dateAdded) {
        this.dateAdded = dateAdded;
    }
}
