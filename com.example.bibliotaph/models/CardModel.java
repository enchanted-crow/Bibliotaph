package com.example.bibliotaph.models;

public class CardModel {
    private String fileName;
    private String dateAdded;

    public CardModel() {  }

    public CardModel(String fileName, String dateAdded) {
        this.fileName = fileName;
        this.dateAdded = dateAdded;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(String dateAdded) {
        this.dateAdded = dateAdded;
    }
}
