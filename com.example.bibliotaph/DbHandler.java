package com.example.bibliotaph;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import com.example.bibliotaph.models.Article;
import com.example.bibliotaph.models.CardModel;
import com.example.bibliotaph.params.AppGlobals;
import java.util.ArrayList;

public class DbHandler extends SQLiteOpenHelper {

    public DbHandler(Context context) {
        super(context, AppGlobals.DB_NAME, null, AppGlobals.DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String create = "Create table if not exists " + AppGlobals.TABLE_NAME + "(" +
                AppGlobals.KEY_NAME + " Varchar Primary key, " + AppGlobals.KEY_BODY +
                " Text, " + AppGlobals.KEY_DATE + " Datetime)";
        db.execSQL(create);
        Log.i("database", "Database created");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void addArticle(Article article) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(AppGlobals.KEY_NAME, article.getFileName());
        values.put(AppGlobals.KEY_BODY, article.getTextBody());
        values.put(AppGlobals.KEY_DATE, article.getDateAdded());
        db.insert(AppGlobals.TABLE_NAME, null, values);
        db.close();
    }

    public ArrayList<CardModel> getAllArticles(int sortIndex) {
        ArrayList<CardModel> cardList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String select;

        if(sortIndex == 0) {
            select = "Select " + AppGlobals.KEY_NAME + ", " + AppGlobals.KEY_DATE
                    + " from " + AppGlobals.TABLE_NAME
                    + " Order by " + AppGlobals.KEY_DATE + " Desc";
        }
        else {
            select = "Select " + AppGlobals.KEY_NAME + ", " + AppGlobals.KEY_DATE
                    + " from " + AppGlobals.TABLE_NAME
                    + " Order by Upper(" + AppGlobals.KEY_NAME + ")" + " Asc";
        }

        Cursor cursor = db.rawQuery(select, null);

        int articleNameIndex = cursor.getColumnIndex(AppGlobals.KEY_NAME);
        int dateAddedIndex = cursor.getColumnIndex(AppGlobals.KEY_DATE);

        if(cursor.moveToFirst()) {
            do {
                CardModel card = new CardModel();
                card.setFileName(cursor.getString(articleNameIndex));
                card.setDateAdded(cursor.getString(dateAddedIndex));
                cardList.add(card);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return cardList;
    }

    public String getArticleBody(String articleName) {
        SQLiteDatabase db = this.getReadableDatabase();
        String select = "Select " + AppGlobals.KEY_BODY + " from " +
                AppGlobals.TABLE_NAME + " where " + AppGlobals.KEY_NAME +
                " = '" + articleName + "'";

        Cursor cursor = db.rawQuery(select, null);
        int textBodyIndex = cursor.getColumnIndex(AppGlobals.KEY_BODY);
        String textBody = null;
        if(cursor.moveToFirst()) {
            textBody = cursor.getString(textBodyIndex);
        }
        cursor.close();
        db.close();
        return textBody;
    }

    public void deleteArticle(String articleName) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(AppGlobals.TABLE_NAME, AppGlobals.KEY_NAME+"=?", new String[]{articleName});
        db.close();
    }
}
