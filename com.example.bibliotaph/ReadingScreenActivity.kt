package com.example.bibliotaph

import android.os.Bundle
import android.widget.TextView
import android.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity

class ReadingScreenActivity : AppCompatActivity() {

    private lateinit var articleBody : TextView
    private lateinit var toolbar: Toolbar

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
            toolbar = findViewById(R.id.toolbar2)
            toolbar.title = fileName
        } catch (e : Exception) {
            e.printStackTrace()
        }
        Thread {
            articleBody = findViewById(R.id.textView3)

            articleBody.text = textBody
        }.start()

    }
}
