package com.example.bibliotaph

import android.content.SharedPreferences
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.bibliotaph.textViews.CustomTextView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList
import kotlin.math.roundToInt

class ReadingScreenActivity : AppCompatActivity() {

    private lateinit var articleBody : CustomTextView
    private lateinit var toolbar : androidx.appcompat.widget.Toolbar
    private lateinit var buttonPlay : FloatingActionButton
    private lateinit var buttonPause : FloatingActionButton
    private lateinit var buttonNext : FloatingActionButton
    private lateinit var buttonPrev : FloatingActionButton
    private lateinit var tts : TextToSpeech
    private lateinit var fileName : String
    private lateinit var textBody : String
    private lateinit var checkPoints : ArrayList<Int>
    private lateinit var thread: Thread
    private var selectPauseColor : Int = R.color.rd_screen_selected_pause
    private var selectPlayColor : Int = R.color.rd_screen_selected_play

    private var currentSentence : Int = 0
    private var isPaused : Boolean = true

    //saved variables
    private var speechRate : Float = 1.0f
    private var pitch : Float = 1.0f
    private var pauseAfter : Int = 0

    companion object {
        const val SHARED_PREFS : String = "com.example.bibliotaph.readingPrefs"
        const val ARTICLE_NAME : String = "com.example.bibliotaph.articleName"
        const val CURRENT_SENTENCE : String = "com.example.bibliotaph.currentSentence"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reading_screen)
        loadData()
        initTTS()
        initButtons()
        displayArticle()
        initToolbar()
    }

    private fun loadData() {
        val sharedPreferences = getSharedPreferences(SettingsActivity.SHARED_PREFS, MODE_PRIVATE)

        speechRate = sharedPreferences.getFloat(SettingsActivity.SPEECHRATE, SettingsActivity.DEFAULT_SPEECHRATE)
        pitch = sharedPreferences.getFloat(SettingsActivity.PITCH, SettingsActivity.DEFAULT_PITCH)
        pauseAfter = sharedPreferences.getInt(SettingsActivity.PAUSEAFTER, SettingsActivity.DEFAULT_PAUSE_AFTER)
    }


    private fun play(startingIndex: Int = currentSentence) {
        toggleVisibility(true)
        isPaused = false

        if (pauseAfter == 1) {
            thread = Thread {
                val i = currentSentence

                tts.speak(textBody.substring(checkPoints[i], checkPoints[i+1]), TextToSpeech.QUEUE_ADD, null, null)
                runOnUiThread {
                    articleBody.setHighlightedText(textBody, checkPoints[i], checkPoints[i+1],
                        ContextCompat.getColor(this@ReadingScreenActivity, selectPlayColor))
                }

                TimeUnit.SECONDS.sleep(1L)
                @Suppress("ControlFlowWithEmptyBody")
                while (tts.isSpeaking);
                currentSentence++
                if (i == checkPoints.size-1) {
                    runOnUiThread { articleBody.text = textBody }
                    currentSentence = 0
                }
                runOnUiThread { pause() }
            }
        }

        else if (pauseAfter == 2) {
            thread = Thread {
                var i = startingIndex
                while (i < checkPoints.size-1 && !isPaused) {
                    currentSentence = i
                    tts.speak(textBody.substring(checkPoints[i], checkPoints[i+1]), TextToSpeech.QUEUE_ADD, null, null)
                    runOnUiThread {
                        articleBody.setHighlightedText(textBody, checkPoints[i], checkPoints[i+1],
                            ContextCompat.getColor(this@ReadingScreenActivity, selectPlayColor))
                    }

                    TimeUnit.SECONDS.sleep(1L)
                    @Suppress("ControlFlowWithEmptyBody")
                    while (tts.isSpeaking);
                    i++
                    if(textBody[checkPoints[i]] == '\n') {
                        currentSentence = i
                        break
                    }
                }
                if (currentSentence+1 == checkPoints.size)
                    currentSentence = 0
                runOnUiThread { pause() }
            }
        }

        else {
            thread = Thread {
                var i = startingIndex
                while (i < checkPoints.size-1 && !isPaused) {
                    currentSentence = i

                    tts.speak(textBody.substring(checkPoints[i], checkPoints[i+1]), TextToSpeech.QUEUE_ADD, null, null)
                    runOnUiThread {
                        articleBody.setHighlightedText(textBody, checkPoints[i], checkPoints[i+1],
                            ContextCompat.getColor(this@ReadingScreenActivity, selectPlayColor))
                    }

                    TimeUnit.SECONDS.sleep(1L)
                    @Suppress("ControlFlowWithEmptyBody")
                    while (tts.isSpeaking);
                    i++
                    if (i == checkPoints.size-1) {
                        runOnUiThread {
                            toggleVisibility(false)
                            articleBody.text = textBody
                        }
                        currentSentence = 0
                    }
                }
            }
        }
        thread.start()
    }

    private fun pause() {
        toggleVisibility(false)
        // do other pause stuff
        isPaused = true
        tts.stop()
        articleBody.setHighlightedText(textBody, checkPoints[currentSentence], checkPoints[currentSentence+1],
            ContextCompat.getColor(this@ReadingScreenActivity, selectPauseColor))
    }

    private fun pauseAndPlay() {
        isPaused = true
        tts.stop()
        @Suppress("ControlFlowWithEmptyBody")
        while (thread.isAlive);
        play()
    }

    private fun goNext() {
        if (currentSentence < checkPoints.size-1)
            currentSentence++
        if(isPaused)
            articleBody.setHighlightedText(textBody, checkPoints[currentSentence], checkPoints[currentSentence+1],
                ContextCompat.getColor(this@ReadingScreenActivity, selectPauseColor))
        else
            pauseAndPlay()
    }

    private fun goPrev() {
        if(currentSentence > 0)
            currentSentence--
        if(isPaused)
            articleBody.setHighlightedText(textBody, checkPoints[currentSentence], checkPoints[currentSentence+1],
                ContextCompat.getColor(this@ReadingScreenActivity, selectPauseColor))
        else
            pauseAndPlay()
    }

    private fun toggleVisibility(toPlay : Boolean) {
        if(toPlay) {
            buttonPlay.visibility = View.INVISIBLE
            buttonPause.visibility = View.VISIBLE
        }
        else {
            buttonPause.visibility = View.INVISIBLE
            buttonPlay.visibility = View.VISIBLE
        }
    }

    private fun markEndOfSentences() {
        checkPoints = ArrayList()
        val punctuations = charArrayOf('.', '?', '!')
        checkPoints.add(0)
        for (i in textBody.indices)
            for (punctuation in punctuations)
                if (punctuation == textBody[i])
                    checkPoints.add(i+1)
    }

    private fun displayArticle() {
        val dbHandler = MainActivity.dbHandler
        val intent = intent
        val source = intent.getIntExtra(MainActivity.SOURCE, 0)

        if (source == 0) {
            loadRecentlyPlayedData()
            Log.i("Current sentence", currentSentence.toString())

            textBody = dbHandler.getArticleBody(fileName)
            markEndOfSentences()
            initText()
            if(intent.getBooleanExtra(MainActivity.PLAY, false))
                play()
            else
                articleBody.setHighlightedText(textBody, checkPoints[currentSentence], checkPoints[currentSentence+1],
                    ContextCompat.getColor(this@ReadingScreenActivity, selectPauseColor))
        }
        else {
            fileName = intent.getStringExtra(MainActivity.TITLE)!!
            currentSentence = 0
            textBody = dbHandler.getArticleBody(fileName)
            markEndOfSentences()
            initText()
        }
    }

    private fun initText() {
        articleBody = findViewById(R.id.article_body)
        articleBody.text = textBody
        articleBody.setOnTouchListener(object : OnTouchListener {
            private val gestureDetector =
                GestureDetector(this@ReadingScreenActivity, object : SimpleOnGestureListener() {
                    override fun onDoubleTap(e: MotionEvent): Boolean {
                        Toast.makeText(applicationContext, "onDoubleTap", Toast.LENGTH_SHORT).show()
                        super.onDoubleTap(e)
                        return true
                    }
                })
            override fun onTouch(v: View, event: MotionEvent): Boolean {
                v.performClick()
                if (gestureDetector.onTouchEvent(event)) {
                    val layout = (v as TextView).layout
                    val x = event.x.roundToInt()
                    val y = event.y.roundToInt()
                    if (layout != null) {
                        val line = layout.getLineForVertical(y)
                        val offset = layout.getOffsetForHorizontal(line, x.toFloat())

                        var low = 0
                        var high = checkPoints.size
                        while (low < high) {
                            val mid = (low+high) shr 1
                            if (offset >= checkPoints[mid])
                                low = mid+1
                            else
                                high = mid
                        }
                        currentSentence = low-1
                        if(isPaused)
                            articleBody.setHighlightedText(textBody, checkPoints[low-1], checkPoints[low],
                                ContextCompat.getColor(this@ReadingScreenActivity, selectPauseColor))
                        else
                            pauseAndPlay()
                    }
                }
                return true
            }
        })
    }

    private fun initToolbar() {
        toolbar = findViewById(R.id.reading_screen_top_toolbar)
        toolbar.title = fileName
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    }

    private fun initTTS() {
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
        tts.setSpeechRate(speechRate)
        tts.setPitch(pitch)
    }

    private fun initButtons() {
        buttonPlay = findViewById(R.id.play_recent_button)
        buttonPause = findViewById(R.id.fab_pause_button)
        buttonNext = findViewById(R.id.fab_next_button)
        buttonPrev = findViewById(R.id.fab_previous_button)

        buttonPlay.setOnClickListener { play() }
        buttonPause.setOnClickListener { pause() }
        buttonNext.setOnClickListener { goNext() }
        buttonPrev.setOnClickListener { goPrev() }
    }

    private fun saveRecentlyPlayedData() {
        val sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPreferences.edit()

        editor.putString(ARTICLE_NAME, fileName)
        editor.putInt(CURRENT_SENTENCE, currentSentence)

        editor.apply()
    }

    private fun loadRecentlyPlayedData() {
        val sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE)

        fileName = sharedPreferences.getString(ARTICLE_NAME, "Article Name")!!
        currentSentence = sharedPreferences.getInt(CURRENT_SENTENCE, 0)
    }

    override fun onDestroy() {
        super.onDestroy()
        isPaused = true
        tts.stop()
        tts.shutdown()
        saveRecentlyPlayedData()
    }
}
