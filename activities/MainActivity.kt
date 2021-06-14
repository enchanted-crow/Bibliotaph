package com.example.myapp

import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapp.PDFProcessor.article
import com.example.myapp.adapters.MyAdapter
import com.example.myapp.models.Article
import com.example.myapp.models.CardModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView

    private lateinit var addButton : FloatingActionButton
    private lateinit var addPDFButton : FloatingActionButton
    private lateinit var addArticleButton : FloatingActionButton

    private val rotateOpen : Animation by lazy { AnimationUtils.loadAnimation(this, R.anim.rotate_open_anim)}
    private val rotateClose : Animation by lazy { AnimationUtils.loadAnimation(this, R.anim.rotate_close_anim)}
    private val fromBottom : Animation by lazy { AnimationUtils.loadAnimation(this, R.anim.from_bottom_anim)}
    private val toBottom : Animation by lazy { AnimationUtils.loadAnimation(this, R.anim.to_bottom_anim)}

    private var addButtonExpanded : Boolean = false

    private lateinit var dbHandler : DbHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val list = ArrayList<CardModel>(1000)

//        for(i in 1..10) {
//            val card = CardModel ("Article Name", "Website Name", "Date Added")
//            list.add(card)
//        }

        dbHandler = DbHandler(this);
        var articleList = dbHandler.allArticles;
        for(article in articleList) {
            val card = CardModel(article.fileName, article.dateAdded)
            list.add(card);
        }

        recyclerView = findViewById(R.id.recycler_view)
        val adapter = MyAdapter(list, this)
        recyclerView.adapter = adapter

        val linearLayoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = linearLayoutManager

        addButtonInit()
    }

    private fun addButtonInit() {
        addButton = findViewById(R.id.add_pdf_nd_article_button)
        addButton.setOnClickListener {
            onAddButtonClicked(addButtonExpanded)
        }

        addPDFButton = findViewById(R.id.add_pdf_button)
        addPDFButton.setOnClickListener {
            val toast = Toast.makeText(this, "PDF!", Toast.LENGTH_SHORT)
            toast.show()


        }

        addArticleButton = findViewById(R.id.add_article_button)
        addArticleButton.setOnClickListener {
            val toast = Toast.makeText(this, "Article!", Toast.LENGTH_SHORT)
            toast.show()
        }
    }

    private fun onAddButtonClicked(function: Boolean) {
        setVisibility(addButtonExpanded)
        setAnimation(addButtonExpanded)
//        setClickable(addButtonExpanded)
        addButtonExpanded = !addButtonExpanded
    }

    private fun setAnimation(addButtonExpanded: Boolean) {
        if(!addButtonExpanded) {
            addArticleButton.startAnimation(fromBottom)
            addPDFButton.startAnimation(fromBottom)
            addButton.startAnimation(rotateOpen)
        }
        else {
            addArticleButton.startAnimation(toBottom)
            addPDFButton.startAnimation(toBottom)
            addButton.startAnimation(rotateClose)
        }
    }

    private fun setVisibility(addButtonExpanded: Boolean) {
        if(!addButtonExpanded) {
            addArticleButton.visibility = View.VISIBLE
            addPDFButton.visibility = View.VISIBLE
        }
        else {
            addArticleButton.visibility = View.INVISIBLE
            addPDFButton.visibility = View.INVISIBLE
        }
    }

    private fun setClickable(addButtonExpanded: Boolean) {
        if(!addButtonExpanded) {
            addArticleButton.isClickable = false
            addPDFButton.isClickable = false
        }
        else {
            addArticleButton.isClickable = true
            addPDFButton.isClickable = true
        }
    }
    

//    override fun onCardListener(position: Int) {
//        var intent = Intent (this, ReadingScreenActivity :: class.java )
//        startActivity(intent)
//    }
}
