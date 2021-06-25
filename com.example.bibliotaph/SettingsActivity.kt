package com.example.bibliotaph

import android.content.SharedPreferences
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import android.widget.Button
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.util.*
import kotlin.math.roundToInt

class SettingsActivity : AppCompatActivity() {

    private var toolbar : androidx.appcompat.widget.Toolbar? = null
    private lateinit var tvValueSpeechRate: TextView
    private lateinit var seekbarSpeechRate: SeekBar
    private lateinit var seekbarPitch: SeekBar
    private lateinit var buttonReset: Button
    private lateinit var buttonPlay: Button
    private lateinit var tts : TextToSpeech

    private var speechRate : Float = 1.0f
    private var pitch : Float = 1.0f

    companion object {
        const val SHARED_PREFS : String = "sharedPrefs"
        const val SPEECHRATE : String = "speechRate"
        const val PITCH : String = "pitch"
    }

    private val speechRateStepSize = 0.25f
    private val minSpeechRate = 0.25f
    private val maxSpeechRate = 3.00f

    private val pitchStepSize = 0.25f
    private val minPitch = 0.25f
    private val maxPitch = 2.00f

    private val defaultSpeechRate = 1.0f
    private val defaultPitch = 1.0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        loadData()
        initToolbar()
        initSeekBars()
        initResetButton()
        initPlayButton()
    }

    override fun onDestroy() {
        super.onDestroy()
        saveDate()
    }

    private fun initToolbar() {
        toolbar = findViewById(R.id.settings_toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    }

    private fun initSeekBars() {
        tvValueSpeechRate = findViewById(R.id.tv_value_speech_rate)
        var progressText: String = "%.2f x".format(speechRate)
        tvValueSpeechRate.text = progressText

        seekbarSpeechRate = findViewById(R.id.seekBar_speech_rate)

        seekbarSpeechRate.max = ((maxSpeechRate-minSpeechRate)/speechRateStepSize).roundToInt()
        val speechRateProgress: Int = ((speechRate-minSpeechRate)/speechRateStepSize).roundToInt()
        seekbarSpeechRate.progress = speechRateProgress

        seekbarSpeechRate.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {

            override fun onProgressChanged(seekBar: SeekBar, progress: Int, b: Boolean) {
                speechRate = minSpeechRate+(progress*speechRateStepSize)
                progressText = "%.2f x".format(speechRate)
                tvValueSpeechRate.text = progressText
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
            }
        })

        seekbarPitch = findViewById(R.id.seekBar_pitch)

        seekbarPitch.max = ((maxPitch-minPitch)/pitchStepSize).roundToInt()
        val pitchProgress: Int = ((pitch-minPitch)/pitchStepSize).roundToInt()
        seekbarPitch.progress = pitchProgress

        seekbarPitch.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {

            override fun onProgressChanged(seekBar: SeekBar, progress: Int, b: Boolean) {
                pitch = minPitch+(progress*pitchStepSize)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
            }
        })
    }

    private fun initResetButton() {
        buttonReset = findViewById(R.id.settings_reset_btn)

        buttonReset.setOnClickListener {
            val sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE)

            speechRate = defaultSpeechRate
            pitch = defaultPitch

            val progressText: String = "%.2f x".format(speechRate)
            tvValueSpeechRate.text = progressText
            seekbarSpeechRate.progress = ((speechRate - minSpeechRate) / speechRateStepSize).toInt()

            seekbarPitch.progress = ((pitch - minPitch) / pitchStepSize).toInt()
            Toast.makeText(this, "reset", Toast.LENGTH_SHORT).show()
        }
    }

    private fun initPlayButton() {
        buttonPlay = findViewById(R.id.settings_play_btn)
        buttonPlay.isEnabled = false
        initTTS()
        buttonPlay.setOnClickListener{ speak() }
    }

    private fun speak() {
        val text: String = "This is an example of speech synthesis in English."

        tts.setSpeechRate(speechRate)
        tts.setPitch(pitch)
        val succ = tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)

        Toast.makeText(this, succ.toString(), Toast.LENGTH_SHORT).show()
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
    }

    private fun saveDate() {
        val sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPreferences.edit()

        editor.putFloat(SPEECHRATE, speechRate)
        editor.putFloat(PITCH, pitch)

        editor.apply()
    }

    private fun loadData() {
        val sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE)

        speechRate = sharedPreferences.getFloat(SPEECHRATE, defaultSpeechRate)
        pitch = sharedPreferences.getFloat(PITCH, defaultPitch)
    }
}
