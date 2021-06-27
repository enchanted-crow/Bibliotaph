package com.example.bibliotaph

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
import com.example.bibliotaph.textViews.MyTextView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList
import kotlin.math.roundToInt


class ReadingScreenActivity : AppCompatActivity() {

    private lateinit var articleBody : MyTextView
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

    private var currentSentence : Int = 0
    private var isPaused : Boolean = true

    //saved variables
    private var speechRate : Float = 1.0f
    private var pitch : Float = 1.0f


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reading_screen)
        loadData()
        displayArticle()
        initTTS()
        initButtons()

    }

    private fun loadData() {
        val sharedPreferences = getSharedPreferences(SettingsActivity.SHARED_PREFS, MODE_PRIVATE)

        speechRate = sharedPreferences.getFloat(SettingsActivity.SPEECHRATE, 1.0f)
        pitch = sharedPreferences.getFloat(SettingsActivity.PITCH, 1.0f)
    }


    private fun play(startingIndex: Int = currentSentence) {
        toggleVisibility(true)
        isPaused = false

        thread = Thread {
            var i = startingIndex
            while (i < checkPoints.size-1 && !isPaused) {
                currentSentence = i
                tts.speak(textBody.substring(checkPoints[i], checkPoints[i+1]), TextToSpeech.QUEUE_ADD, null, null)
                Log.i("Current sentence", currentSentence.toString())
                TimeUnit.SECONDS.sleep(1L)
                @Suppress("ControlFlowWithEmptyBody")
                while (tts.isSpeaking);
                i++
            }
        }
        thread.start()

    }

    private fun pause() {
        toggleVisibility(false)

        // do other pause stuff
        isPaused = true
        tts.stop()
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
        if (!isPaused)
            pauseAndPlay()

    }

    private fun goPrev() {
        if(currentSentence > 0)
            currentSentence--
        if (!isPaused)
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

    private fun displayArticle() {
        val intent = intent
        val index = intent.getIntExtra(MainActivity.index, 0)
        fileName = MainActivity.articleList[index].fileName
        textBody = MainActivity.articleList[index].textBody
        checkPoints = ArrayList()

        val punctuations = charArrayOf('.', '?', '!')
        checkPoints.add(0)
        for (i in textBody.indices)
            for (punctuation in punctuations)
                if (punctuation == textBody[i])
                    checkPoints.add(i+1)

        toolbar = findViewById(R.id.reading_screen_top_toolbar)
        toolbar.title = fileName
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

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
                        if(!isPaused)
                            pauseAndPlay()

                        Log.i("Selected sentence", textBody.substring(checkPoints[low-1], checkPoints[low]))
                    }
                }
                return true
            }
        })


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
        buttonPlay = findViewById(R.id.fab_play_button)
        buttonPause = findViewById(R.id.fab_pause_button)
        buttonNext = findViewById(R.id.fab_next_button)
        buttonPrev = findViewById(R.id.fab_previous_button)

        buttonPlay.setOnClickListener { play() }
        buttonPause.setOnClickListener { pause() }
        buttonNext.setOnClickListener { goNext() }
        buttonPrev.setOnClickListener { goPrev() }
    }

    override fun onDestroy() {
        isPaused = true
        tts.stop()
        tts.shutdown()
        super.onDestroy()
    }

}
