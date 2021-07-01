package com.example.bibliotaph;


import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.util.Objects;

public class WebActivity extends AppCompatActivity {

    public static final String TITLE = "com.example.bibliotaph.TITLE";
    public static final String BODY = "com.example.bibliotaph.BODY";
    private WebView webView;

    private void extractTextArticle(String url) {
        LoadingDialog loadingDialog = new LoadingDialog(WebActivity.this);
        loadingDialog.startLoadingDialog();
        new Thread(() -> {
            try {
                StringBuilder content = new StringBuilder();
                Document document = Jsoup.connect(url).get();
                String title = document.title();
                title = title.substring(0, title.lastIndexOf("-")-1);
                Element body = document.getElementById("mw-content-text");
                Elements paragraphs = body.getElementsByTag("p");
                for(Element paragraph: paragraphs) {
                    String p = paragraph.text();
                    if (!p.equals("") && p.charAt(p.length()-1) != '.')
                        p += '.';
                    content.append(p).append("\n");
                }

                String articleBody = content.toString().replaceAll("\\[\\d*]","");

                Intent intent = new Intent(WebActivity.this, MainActivity.class);
                intent.putExtra(TITLE, title);
                intent.putExtra(BODY, articleBody);
                setResult(RESULT_OK, intent);
                finish();
            } catch (IOException e) {
                e.printStackTrace();
            }
            loadingDialog.dismissDialog();
        }).start();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);

        FloatingActionButton button = findViewById(R.id.button);
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
