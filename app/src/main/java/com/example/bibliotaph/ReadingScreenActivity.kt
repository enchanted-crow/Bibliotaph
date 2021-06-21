package com.example.bibliotaph

import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.util.*


class ReadingScreenActivity : AppCompatActivity() {

    private lateinit var articleBody : TextView
    private lateinit var toolbar : androidx.appcompat.widget.Toolbar
    private lateinit var buttonPlay : Button
    private lateinit var myTTS : TextToSpeech

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reading_screen)
        displayArticle()

        buttonPlay = findViewById(R.id.reading_screen_play_button)

        myTTS = TextToSpeech(this, TextToSpeech.OnInitListener { status ->
                if (status == TextToSpeech.SUCCESS) {
                    val result: Int = myTTS.setLanguage(Locale.US)
                    if (result == TextToSpeech.LANG_MISSING_DATA
                        || result == TextToSpeech.LANG_NOT_SUPPORTED
                    ) {
                        Log.e("TTS", "Language not supported")
                    } else {
                        buttonPlay.isEnabled = true
                    }
                } else {
                    Log.e("TTS", "Initialization failed")
                }
            })

        buttonPlay.setOnClickListener { speak() }
    }

    private fun speak() {
        val text: String = articleBody.text.toString()P
        var pitch = SettingsActivity.getPitch()
        if (pitch < 0.1) pitch = 0.1f
        var speed = SettingsActivity.getSpeechRate()
        if (speed < 0.1) speed = 0.1f

        myTTS.setPitch(pitch)
        myTTS.setSpeechRate(speed)
        val succ = myTTS.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)

        Toast.makeText(this, succ.toString(), Toast.LENGTH_SHORT).show()
    }

    private fun displayArticle() {
        val intent = intent
        val index = intent.getIntExtra(MainActivity.index, 0)
        val fileName = MainActivity.articleList[index].fileName
        val textBody = MainActivity.articleList[index].textBody

        toolbar = findViewById(R.id.reading_screen_top_toolbar)
        toolbar.title = fileName
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        Thread {
            articleBody = findViewById(R.id.article_body)
            articleBody.text = textBody
        }.start()
    }

    override fun onDestroy() {
        myTTS.stop()
        myTTS.shutdown()
        super.onDestroy()
    }
}