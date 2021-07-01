package com.example.bibliotaph

import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.text.Layout.JUSTIFICATION_MODE_INTER_WORD
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import java.util.*
import kotlin.math.roundToInt


class SettingsActivity : AppCompatActivity() {

    private var toolbar : androidx.appcompat.widget.Toolbar? = null
    private lateinit var tvValueSpeechRate: TextView
    private lateinit var spnPauseAfterSelected: Spinner
    private lateinit var seekbarSpeechRate: SeekBar
    private lateinit var seekbarPitch: SeekBar
    private lateinit var buttonReset: Button
    private lateinit var buttonPlay: Button
    private lateinit var tts : TextToSpeech
    private lateinit var imageButtonFontDecrease : ImageButton
    private lateinit var imageButtonFontIncrease : ImageButton
    private lateinit var tvSampleText : TextView
    private lateinit var imageButtonLeftAlign : ImageButton
    private lateinit var imageButtonCenterAlign : ImageButton
    private lateinit var imageButtonRightAlign : ImageButton
    private lateinit var imageButtonJustifyAlign : ImageButton

    companion object {
        const val SHARED_PREFS : String = "com.example.bibliotaph.sharedPrefs"
        const val SPEECHRATE : String = "com.example.bibliotaph.speechRate"
        const val PITCH : String = "com.example.bibliotaph.pitch"
        const val PAUSEAFTER : String = "com.example.bibliotaph.pauseAfter"
        const val FONTSIZE : String = "com.example.bibliotaph.fontSize"

        const val DEFAULT_SPEECHRATE : Float = 1.0f
        const val DEFAULT_PITCH : Float = 1.0f
        const val DEFAULT_PAUSE_AFTER : Int = 2
        const val DEFAULT_FONT_SIZE : Float = 18.0f
    }

    private var speechRate : Float = DEFAULT_SPEECHRATE
    private var pitch : Float = DEFAULT_PITCH
    private var pauseAfter : Int = DEFAULT_PAUSE_AFTER
    private var fontSize : Float = DEFAULT_FONT_SIZE

    private val speechRateStepSize = 0.25f
    private val minSpeechRate = 0.25f
    private val maxSpeechRate = 2.00f

    private val pitchStepSize = 0.25f
    private val minPitch = 0.25f
    private val maxPitch = 2.00f
    private val fontStepSize = 1.0f


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        loadData()
        initToolbar()
        initSeekBars()
        initResetButton()
        initPlayButton()
        initSpinners()
        initText()
        initAlignment()
    }

    override fun onDestroy() {
        super.onDestroy()
        saveDate()
        tts.stop()
        tts.shutdown()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun initAlignment() {
        imageButtonLeftAlign = findViewById(R.id.align_left)
        imageButtonCenterAlign = findViewById(R.id.align_center)
        imageButtonRightAlign = findViewById(R.id.align_right)
        imageButtonJustifyAlign = findViewById(R.id.align_justify)

        Log.d("Settings", Gravity.START.toString())
        Log.d("Settings", Gravity.END.toString())
        Log.d("Settings", Gravity.CENTER.toString())
        Log.d("Settings", Gravity.START.toString())

        imageButtonLeftAlign.setOnClickListener {
            tvSampleText.gravity = Gravity.START
        }
        imageButtonRightAlign.setOnClickListener {
            tvSampleText.gravity = Gravity.END
        }
        imageButtonCenterAlign.setOnClickListener {
            tvSampleText.gravity = Gravity.CENTER
        }
        imageButtonJustifyAlign.setOnClickListener {
            tvSampleText.gravity = Gravity.CENTER_VERTICAL or Gravity.START
            tvSampleText.justificationMode = JUSTIFICATION_MODE_INTER_WORD
        }
    }

    private fun initText() {
        imageButtonFontDecrease = findViewById(R.id.fontsize_decrease)
        imageButtonFontIncrease = findViewById(R.id.fontsize_increase)
        tvSampleText = findViewById(R.id.sample_text)

        var tempSize = fontSize/resources.displayMetrics.scaledDensity

        tvSampleText.setTextSize(TypedValue.COMPLEX_UNIT_SP, tempSize)

        imageButtonFontDecrease.setOnClickListener {
            fontSize -= fontStepSize
            tempSize = fontSize/resources.displayMetrics.scaledDensity
            Log.d("FONT", fontSize.toString())
            tvSampleText.setTextSize(TypedValue.COMPLEX_UNIT_SP, tempSize)
        }

        imageButtonFontIncrease.setOnClickListener {
            fontSize += fontStepSize
            tempSize = fontSize/resources.displayMetrics.scaledDensity
            Log.d("FONT", fontSize.toString())
            tvSampleText.setTextSize(TypedValue.COMPLEX_UNIT_SP, tempSize)
        }
    }

    private fun initToolbar() {
        toolbar = findViewById(R.id.settings_toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    }

    private fun initSeekBars() {
        tvValueSpeechRate = findViewById(R.id.tv_value_speech_rate)
        var progressText: String = "%.2fx".format(speechRate)
        tvValueSpeechRate.text = progressText

        seekbarSpeechRate = findViewById(R.id.seekBar_speech_rate)

        seekbarSpeechRate.max = ((maxSpeechRate-minSpeechRate)/speechRateStepSize).roundToInt()
        val speechRateProgress: Int = ((speechRate-minSpeechRate)/speechRateStepSize).roundToInt()
        seekbarSpeechRate.progress = speechRateProgress

        seekbarSpeechRate.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {

            override fun onProgressChanged(seekBar: SeekBar, progress: Int, b: Boolean) {
                speechRate = minSpeechRate + (progress * speechRateStepSize)
                progressText = "%.2fx".format(speechRate)
                tvValueSpeechRate.text = progressText
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}

            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })

        seekbarPitch = findViewById(R.id.seekBar_pitch)

        seekbarPitch.max = ((maxPitch-minPitch)/pitchStepSize).roundToInt()
        val pitchProgress: Int = ((pitch-minPitch)/pitchStepSize).roundToInt()
        seekbarPitch.progress = pitchProgress

        seekbarPitch.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {

            override fun onProgressChanged(seekBar: SeekBar, progress: Int, b: Boolean) {
                pitch = minPitch + (progress * pitchStepSize)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}

            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })
    }

    private fun initResetButton() {
        buttonReset = findViewById(R.id.settings_reset_btn)

        buttonReset.setOnClickListener {
            speechRate = DEFAULT_SPEECHRATE
            pitch = DEFAULT_PITCH

            val progressText: String = "%.2fx".format(speechRate)
            tvValueSpeechRate.text = progressText
            seekbarSpeechRate.progress = ((speechRate - minSpeechRate) / speechRateStepSize).roundToInt()
            seekbarPitch.progress = ((pitch - minPitch) / pitchStepSize).roundToInt()
        }
    }

    private fun initPlayButton() {
        buttonPlay = findViewById(R.id.settings_play_btn)
        buttonPlay.isEnabled = false
        initTTS()
        buttonPlay.setOnClickListener{ speak() }
    }

    private fun speak() {
        val text = "The quick brown fox jumps over the lazy dog."

        tts.setSpeechRate(speechRate)
        tts.setPitch(pitch)
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
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

    private fun initSpinners() {
        spnPauseAfterSelected = findViewById(R.id.pause_after_selection)

        val options = arrayOf("End of Sentence", "End of Paragraph", "End of Article")
        spnPauseAfterSelected.adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, options)
        spnPauseAfterSelected.setSelection(pauseAfter)

        spnPauseAfterSelected.onItemSelectedListener = object : AdapterView.OnItemClickListener, AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                pauseAfter = position
                Log.d("SETTINGS", "pause $position select")
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                Log.d("SETTINGS", "pause none sec")
            }

            override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                Log.d("SETTINGS", "pause $position click")
            }
        }

    }

    private fun saveDate() {
        val sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPreferences.edit()

        editor.putFloat(SPEECHRATE, speechRate)
        editor.putFloat(PITCH, pitch)
        editor.putInt(PAUSEAFTER, pauseAfter)
        editor.putFloat(FONTSIZE, fontSize)

        editor.apply()
    }

    private fun loadData() {
        val sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE)

        speechRate = sharedPreferences.getFloat(SPEECHRATE, DEFAULT_SPEECHRATE)
        pitch = sharedPreferences.getFloat(PITCH, DEFAULT_PITCH)
        pauseAfter = sharedPreferences.getInt(PAUSEAFTER, DEFAULT_PAUSE_AFTER)
        fontSize = sharedPreferences.getFloat(FONTSIZE, DEFAULT_FONT_SIZE)
    }
}
