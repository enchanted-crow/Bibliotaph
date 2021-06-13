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
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;


public class WebActivity extends AppCompatActivity {

    public static final String MSG = "com.example.webcontent.BODY";
    private WebView webView;

    public class DownloadTask extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... urls) {
            String articleBody = "";
            try {
                Document document = Jsoup.connect(urls[0]).get();

                Element body = document.getElementById("mw-content-text");
                Elements paragraphs = body.getElementsByTag("p");
                for(Element paragraph: paragraphs) {
                    articleBody += paragraph.text() + "\n";
                }

                Intent intent = new Intent(WebActivity.this, MainActivity.class);
                intent.putExtra(MSG, articleBody);
                startActivity(intent);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (NullPointerException e) {
                e.printStackTrace();
            }

            return null;
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
            DownloadTask downloadTask = new DownloadTask();
            try {

                downloadTask.execute(url);
            } catch (Exception e) {
                e.printStackTrace();
            }

        });

        webView = findViewById(R.id.webView);
        webView.getSettings().getJavaScriptEnabled();
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl("https://en.wikipedia.org/");
    }
}
