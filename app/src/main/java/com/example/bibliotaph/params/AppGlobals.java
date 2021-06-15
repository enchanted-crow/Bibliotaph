package com.example.bibliotaph.params;

import com.example.bibliotaph.DbHandler;
import com.example.bibliotaph.models.CardModel;

import java.util.ArrayList;

public class AppGlobals {
    public static final int DB_VERSION = 1;
    public static final String DB_NAME = "articleinfo";
    public static final String TABLE_NAME = "articleinfo";


    public static final String KEY_NAME = "filename";
    public static final String KEY_BODY = "textbody";
    public static final String KEY_DATE = "added";
    public static DbHandler myDB = null;
    public static ArrayList <CardModel> cardList = new ArrayList <CardModel> (1000);
}