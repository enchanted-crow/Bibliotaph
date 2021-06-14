package com.example.bibliotaph;


import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
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

    public static final String TITLE = "com.example.pdfFinder.TITLE";
    public static final String BODY = "com.example.pdfFinder.BODY";
    private WebView webView;

    private void extractTextArticle(String url) {
        new Thread(() -> {
            try {
                StringBuilder articleBody = new StringBuilder();
                Document document = Jsoup.connect(url).get();
                String title = document.title();
                Element body = document.getElementById("mw-content-text");
                Elements paragraphs = body.getElementsByTag("p");
                for(Element paragraph: paragraphs) {
                    articleBody.append(paragraph.text()).append("\n");
                }

                Intent intent = new Intent(WebActivity.this, MainActivity.class);
                intent.putExtra(TITLE, title);
                intent.putExtra(BODY, articleBody.toString());
                setResult(RESULT_OK, intent);
                finish();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }).start();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);

        Button button = findViewById(R.id.button);
        button.setOnClickListener(v -> {
            String url = webView.getUrl();
            Log.i("url", url);
            extractTextArticle(url);
        });

        webView = findViewById(R.id.webView);
        webView.getSettings().getJavaScriptEnabled();
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl("https://en.wikipedia.org/");
    }
}