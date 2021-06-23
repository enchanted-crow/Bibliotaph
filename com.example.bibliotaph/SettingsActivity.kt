package com.example.bibliotaph

import android.content.SharedPreferences
import android.os.Bundle
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class SettingsActivity : AppCompatActivity() {

    private var toolbar : androidx.appcompat.widget.Toolbar? = null
    private lateinit var tvValueSpeechRate: TextView
    private lateinit var seekbarSpeechRate: SeekBar
    private lateinit var seekbarPitch: SeekBar

    private var speechRate : Float = 0.0f
    private var pitch : Float = 0.0f
    private var initCheckSeekBar : Boolean = false

    companion object {
        const val SHARED_PREFS : String = "sharedPrefs"
        const val SPEECHRATE : String = "speechRate"
        const val PITCH : String = "pitch"
    }

    private val speechRateStepSize = 25

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
        seekbarSpeechRate = findViewById(R.id.seekBar_speech_rate)
        seekbarPitch = findViewById(R.id.seekBar_pitch)

        seekbarSpeechRate.max = ((300 - speechRateStepSize.toFloat()) / speechRateStepSize.toFloat()).toInt()

        if(initCheckSeekBar) {
            seekbarSpeechRate.progress = (speechRate * seekbarSpeechRate.max).toInt()
            seekbarPitch.progress = (pitch * seekbarPitch.max).toInt()

            val v = speechRate / seekbarSpeechRate.max
            tvValueSpeechRate.text = "$v x"
        }
        else {
            seekbarSpeechRate.progress = (100 / speechRateStepSize)

            speechRate = seekbarSpeechRate.progress.toFloat()*3 / seekbarSpeechRate.max
            pitch = seekbarPitch.progress.toFloat() / seekbarPitch.max
        }

        seekbarSpeechRate.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {

            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                val v = speechRateStepSize * (i.toFloat() + 1) / 100
                tvValueSpeechRate.text = "$v x"

                speechRate = seekbarSpeechRate.progress.toFloat()*3 / seekbarSpeechRate.max
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                Toast.makeText(applicationContext, "start tracking", Toast.LENGTH_SHORT).show()
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                Toast.makeText(applicationContext, "stop tracking", Toast.LENGTH_SHORT).show()
            }
        })

        seekbarPitch.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {

            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                pitch = seekbarPitch.progress.toFloat() / seekbarPitch.max
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                Toast.makeText(applicationContext, "start tracking", Toast.LENGTH_SHORT).show()
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                Toast.makeText(applicationContext, "stop tracking", Toast.LENGTH_SHORT).show()
            }
        })
        initCheckSeekBar = true
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
