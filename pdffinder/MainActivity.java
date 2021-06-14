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

import com.example.pdffinder.dbhandler.DbHandler;
import com.example.pdffinder.model.Article;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class MainActivity extends AppCompatActivity {

    private InputStream inputStream;
    private ArrayList<String> fileNames = new ArrayList<>();
    private DbHandler dbHandler;
    private ArrayAdapter<String> arrayAdapter;
    private Article article;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

    private void callChooseFileFromDevice() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/pdf");
        pdfResultLauncher.launch(intent);
    }

    private void callChooseArticleFromWeb() {
        Intent intent = new Intent(MainActivity.this, WebActivity.class);
        articleResultLauncher.launch(intent);
    }

    private String extractPdfName(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
        cursor.moveToFirst();
        String pdfName = cursor.getString(nameIndex);
        cursor.close();
        return pdfName.substring(0, pdfName.lastIndexOf("."));
    }

    private void extractTextPdfFile(Uri uri) {
        try {
            inputStream = MainActivity.this.getContentResolver().openInputStream(uri);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        new Thread(() -> {
            StringBuilder fileContent = new StringBuilder();
            PdfReader reader;
            try {
                reader = new PdfReader(inputStream);

                int pages = reader.getNumberOfPages();

                for(int i=1; i<=pages; i++) {
                    fileContent.append(PdfTextExtractor.getTextFromPage(reader, i).trim()).append("\n");
                }
                reader.close();

                article.setTextBody(fileContent.toString());

            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button pdfAdder = findViewById(R.id.pdfAdder);
        Button articleAdder = findViewById(R.id.articleAdder);
        ListView listView = findViewById(R.id.listView);

        pdfAdder.setOnClickListener(v -> callChooseFileFromDevice());

        articleAdder.setOnClickListener(v -> callChooseArticleFromWeb());

        dbHandler = new DbHandler(MainActivity.this);
        List<Article> articleList = dbHandler.getAllArticles();
        for(Article article: articleList) {
            fileNames.add(article.getFileName());
        }

        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, fileNames);
        listView.setAdapter(arrayAdapter);


    }

    ActivityResultLauncher<Intent> pdfResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if(result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if(data != null) {
                            Uri uri = data.getData();
                            String pdfName = extractPdfName(uri);
                            article = new Article();
                            article.setFileName(pdfName);
                            extractTextPdfFile(uri);
                            String dateAdded = dateFormat.format(new Date());
                            article.setDateAdded(dateAdded);
                            dbHandler.addArticle(article);
                            fileNames.add(pdfName);
                            arrayAdapter.notifyDataSetChanged();
                        }
                    }
                }
            }
    );

    ActivityResultLauncher<Intent> articleResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if(result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if(data != null) {

                            String articleName = data.getStringExtra(WebActivity.TITLE);
                            String articleBody = data.getStringExtra(WebActivity.BODY);
                            String dateAdded = dateFormat.format(new Date());
                            article = new Article(articleName, articleBody, dateAdded);
                            dbHandler.addArticle(article);
                            fileNames.add(articleName);
                            arrayAdapter.notifyDataSetChanged();
                        }
                    }
                }
            }
    );

   

}
