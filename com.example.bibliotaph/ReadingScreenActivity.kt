package com.example.bibliotaph

import android.os.Bundle
import android.widget.TextView
import android.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity

class ReadingScreenActivity : AppCompatActivity() {

    private lateinit var articleBody : TextView
    private lateinit var toolbar : androidx.appcompat.widget.Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reading_screen)
        displayArticle()
    }

    private fun displayArticle() {
        val intent = intent
        val index = intent.getIntExtra(MainActivity.index, 0)
        val fileName = MainActivity.articleList[index].fileName
        val textBody = MainActivity.articleList[index].textBody

        try {
            toolbar = findViewById(R.id.reading_screen_top_toolbar)
            toolbar.title = fileName
            setSupportActionBar(toolbar)
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        } catch (e : Exception) {
            e.printStackTrace()
        }

        articleBody = findViewById(R.id.article_body)
        articleBody.text = textBody

    }

}
