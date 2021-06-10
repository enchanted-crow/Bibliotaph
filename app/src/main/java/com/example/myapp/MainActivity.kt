package com.example.myapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toolbar
import androidx.cardview.widget.CardView

class MainActivity : AppCompatActivity() {

    private lateinit var card : CardView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        card = findViewById(R.id.card_view)
        card.setOnClickListener {
            Log.d("card", "nunu tipse")
        }
    }
}