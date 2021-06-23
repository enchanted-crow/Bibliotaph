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
    private lateinit var tts : TextToSpeech

    //saved variables
    private var speechRate : Float = 1.0f
    private var pitch : Float = 1.0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reading_screen)

        loadData()
        displayArticle()

        buttonPlay = findViewById(R.id.reading_screen_play_button)
        tts = TextToSpeech(this) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val result: Int = tts.setLanguage(Locale.ENGLISH)
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
        }

        buttonPlay.setOnClickListener { speak() }
    }

    private fun loadData() {
        val sharedPreferences = getSharedPreferences(SettingsActivity.SHARED_PREFS, MODE_PRIVATE)

        speechRate = sharedPreferences.getFloat(SettingsActivity.SPEECHRATE, 1.0f)
        pitch = sharedPreferences.getFloat(SettingsActivity.PITCH, 1.0f)
    }

    private fun speak() {
        val text: String = articleBody.text.toString()


//        if (speechRate < 0.1f) speechRate = 0.1f
//        if (pitch < 0.1f) pitch = 0.1f

        Log.d("TTS", "Speech rate: $speechRate")
        Log.d("TTS", "Pitch: $pitch")


        tts.setSpeechRate(speechRate)
        tts.setPitch(pitch)
        val succ = tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)

        Toast.makeText(this, succ.toString(), Toast.LENGTH_SHORT).show()
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

    override fun onDestroy() {
        tts.stop()
        tts.shutdown()
        super.onDestroy()
    }

}
