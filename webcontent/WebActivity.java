package com.example.webcontent;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;


public class WebActivity extends AppCompatActivity {

    public static final String MSG = "com.example.webcontent.TITLE";
    private WebView webView;

    public static class DownloadTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            String articleName = "Failed";
            try {
                Document document = Jsoup.connect(urls[0]).get();
                articleName = document.title();

                Log.i("title", articleName);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
            return articleName;
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);

        Button button = findViewById(R.id.button);
        button.setOnClickListener(v -> {
            String url = webView.getUrl();
            Log.i("url", url);
            String articleName = "";
            DownloadTask downloadTask = new DownloadTask();
            try {
                articleName = downloadTask.execute(url).get();
            } catch (Exception e) {
                e.printStackTrace();
            }
            Intent intent = new Intent(WebActivity.this, MainActivity.class);
            intent.putExtra(MSG, articleName);
            startActivity(intent);
        });

        webView = findViewById(R.id.webView);
        webView.getSettings().getJavaScriptEnabled();
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl("https://en.wikipedia.org/");
    }
}