package com.example.bibliotaph

import android.os.Bundle
import android.widget.TextView
import android.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import com.example.bibliotaph.params.AppGlobals
import com.example.bibliotaph.params.AppGlobals.myDB

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
        val index = intent.getIntExtra("cardArrListIndex", 0)
        val fileName = AppGlobals.cardList[index].fileName

//        this shit doesnt work :/ somehow findViewById(R.id.appbar) eta null return kore
        try {
            toolbar = findViewById(R.id.toolbar2)
            toolbar.title = fileName
        } catch (e : Exception) {
            e.printStackTrace()
        }

        val textBody = myDB.getTextBodyFromFilename(fileName)

        articleBody = findViewById(R.id.textView3)
        articleBody.text = textBody
    }
}
