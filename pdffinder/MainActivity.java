package com.example.pdffinder;


import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.pdffinder.dbhandler.DbHandler;
import com.example.pdffinder.model.Article;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    private TextView filePath;
    private ArrayList<String> fileNames = new ArrayList<String>();
    private DbHandler dbHandler;

    private void callChooseFileFromDevice() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/pdf");
        activityResultLauncher.launch(intent);
    }

    private String extractPdfName(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
        cursor.moveToFirst();
        String pdfName = cursor.getString(nameIndex);
        cursor.close();
        return pdfName.substring(0, pdfName.lastIndexOf("."));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button finder = findViewById(R.id.finder);
        filePath = findViewById(R.id.filepath);
        ListView listView = findViewById(R.id.listView);

        finder.setOnClickListener(v -> callChooseFileFromDevice());

        dbHandler = new DbHandler(MainActivity.this);
        List<Article> articleList = dbHandler.getAllArticles();
        for(Article article: articleList) {
            fileNames.add(article.getFileName());
        }

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, fileNames);
        listView.setAdapter(arrayAdapter);

    }

    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if(result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if(data != null) {
                            Uri uri = data.getData();
                            filePath.setText(uri.toString());
                            String pdfName = extractPdfName(uri);
                            Article article = new Article(uri.toString(), pdfName);
                            dbHandler.addArticle(article);
                        }
                    }
                }
            }
    );
}