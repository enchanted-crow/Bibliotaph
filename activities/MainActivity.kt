package com.example.myapp

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.View
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapp.adapters.MyAdapter
import com.example.myapp.models.CardModel

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val list = ArrayList <CardModel> (100)

        for(i in 1..10) {
//            val card = CardModel (i.toString(), (i*i).toString(), (i*i*i).toString())
            val card = CardModel ("Article Name", "Website Name", "Date Added")
            list.add(card)
        }

        Log.d("size", list.size.toString())

        recyclerView = findViewById(R.id.recycler_view)
        val adapter = MyAdapter(list, this)
        recyclerView.adapter = adapter

        val linearLayoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = linearLayoutManager



//        recyclerView.setOnClickListener { parent:AdapterView
//
//        }
    }

    companion object {
        public fun onClickListener_Cards(articleName : String) {
//            Toast.makeText(this, articleName, Toast.LENGTH_SHORT).show()
            Log.d("dhur", articleName)
        }
    }
}
