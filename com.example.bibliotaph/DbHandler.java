package com.example.bibliotaph;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import com.example.bibliotaph.models.Article;
import com.example.bibliotaph.params.Params;
import java.util.ArrayList;

public class DbHandler extends SQLiteOpenHelper {

    public DbHandler(Context context) {
        super(context, Params.DB_NAME, null, Params.DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String create = "Create table if not exists " + Params.TABLE_NAME + "(" +
                Params.KEY_NAME + " Varchar Primary key, " + Params.KEY_BODY +
                " Text, " + Params.KEY_DATE + " Datetime)";
        db.execSQL(create);
        Log.i("database", "Database created");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void addArticle(Article article) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Params.KEY_NAME, article.getFileName());
        values.put(Params.KEY_BODY, article.getTextBody());
        values.put(Params.KEY_DATE, article.getDateAdded());
        db.insert(Params.TABLE_NAME, null, values);
        Log.i("database", article.getTextBody());
    }

    public ArrayList<Article> getAllArticles() {
        ArrayList<Article> articleList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String select = "Select * from " + Params.TABLE_NAME;
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
}