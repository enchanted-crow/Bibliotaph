package com.example.bibliotaph

import android.content.SharedPreferences
import android.os.Bundle
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlin.math.roundToInt

class SettingsActivity : AppCompatActivity() {

    private var toolbar : androidx.appcompat.widget.Toolbar? = null
    private lateinit var tvValueSpeechRate: TextView
    private lateinit var seekbarSpeechRate: SeekBar
    private lateinit var seekbarPitch: SeekBar

    private var speechRate : Float = 1.0f
    private var pitch : Float = 1.0f

    companion object {
        const val SHARED_PREFS : String = "sharedPrefs"
        const val SPEECHRATE : String = "speechRate"
        const val PITCH : String = "pitch"
    }

    private val speechRateStepSize = 0.25f
    private val minSpeechRate = 0.25f
    private val maxSpeechRate = 2.00f

    private val pitchStepSize = 0.25f
    private val minPitch = 0.25f
    private val maxPitch = 2.00f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        loadData()
        initToolbar()
        initSeekBars()
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
                Toast.makeText(applicationContext, "start tracking", Toast.LENGTH_SHORT).show()
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                Toast.makeText(applicationContext, "stop tracking", Toast.LENGTH_SHORT).show()
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
                Toast.makeText(applicationContext, "start tracking", Toast.LENGTH_SHORT).show()
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                Toast.makeText(applicationContext, "stop tracking", Toast.LENGTH_SHORT).show()
            }
        })
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

        speechRate = sharedPreferences.getFloat(SPEECHRATE, 1.0f)
        pitch = sharedPreferences.getFloat(PITCH, 1.0f)
    }
}
