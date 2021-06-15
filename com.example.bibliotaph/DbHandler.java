package com.example.bibliotaph;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import com.example.bibliotaph.models.Article;
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
        Log.i("database", article.getTextBody());
    }

    public ArrayList<Article> getAllArticles() {
        ArrayList<Article> articleList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String select = "Select * from " + AppGlobals.TABLE_NAME;
        Cursor cursor = db.rawQuery(select, null);

        if(cursor.moveToFirst()) {
            do {
                Article article = new Article();
                article.setFileName(cursor.getString(0));
                article.setTextBody(cursor.getString(1));
                article.setDateAdded(cursor.getString(2));
                articleList.add(article);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return articleList;
    }

    public String getTextBodyFromFilename(String fileName) {
        String query = "Select " + AppGlobals.KEY_BODY + " from " + AppGlobals.TABLE_NAME + " WHERE " + AppGlobals.KEY_NAME + " = \"" + fileName + "\"";
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            try {
                String ret = cursor.getString(0);
                cursor.close();
                return ret;
            } catch (Exception e) {
                e.printStackTrace();
                cursor.close();
            }
        }
        return "ERROR!";
    }
}
