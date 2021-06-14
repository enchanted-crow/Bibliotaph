package com.example.myapp

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapp.adapters.MyAdapter
import com.example.myapp.models.Article
import com.example.myapp.models.CardModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.itextpdf.text.pdf.PdfReader
import com.itextpdf.text.pdf.parser.PdfTextExtractor
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView

    private lateinit var addButton : FloatingActionButton
    private lateinit var addPDFButton : FloatingActionButton
    private lateinit var addArticleButton : FloatingActionButton

    private val rotateOpen : Animation by lazy { AnimationUtils.loadAnimation(
        this,
        R.anim.rotate_open_anim
    )}
    private val rotateClose : Animation by lazy { AnimationUtils.loadAnimation(
        this,
        R.anim.rotate_close_anim
    )}
    private val fromBottom : Animation by lazy { AnimationUtils.loadAnimation(
        this,
        R.anim.from_bottom_anim
    )}
    private val toBottom : Animation by lazy { AnimationUtils.loadAnimation(
        this,
        R.anim.to_bottom_anim
    )}

    private var addButtonExpanded : Boolean = false
    private lateinit var myAdapter : MyAdapter
    private val arrayList = ArrayList<CardModel>(1000)

    // Tahmids variables
    private var inputStream: InputStream? = null
    private val fileNames = java.util.ArrayList<String>()
    private var dbHandler: DbHandler? = null
    private var article: Article? = null
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        for(i in 1..10) {
//            val card = CardModel ("Article Name", "Date Added")
//            list.add(card)
//        }

        dbHandler = DbHandler(this);
        var articleList = dbHandler!!.allArticles;
        for(article in articleList) {
            val card = CardModel(article.fileName, article.dateAdded)
            arrayList.add(card);
        }

        recyclerView = findViewById(R.id.recycler_view)
        myAdapter = MyAdapter(arrayList, this)
        recyclerView.adapter = myAdapter

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

            callChooseFileFromDevice()
        }

        addArticleButton = findViewById(R.id.add_article_button)
        addArticleButton.setOnClickListener {
            val toast = Toast.makeText(this, "Article!", Toast.LENGTH_SHORT)
            toast.show()

            callChooseArticleFromWeb()
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

    // tahmi'd methods

    private fun callChooseFileFromDevice() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "application/pdf"
        pdfResultLauncher.launch(intent)
    }

    private fun callChooseArticleFromWeb() {
        val intent = Intent(this@MainActivity, WebActivity::class.java)
        articleResultLauncher.launch(intent)
    }

    private fun extractPdfName(uri: Uri): String {
        val cursor = contentResolver.query(uri, null, null, null, null)
        val nameIndex = cursor!!.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        cursor.moveToFirst()
        val pdfName = cursor.getString(nameIndex)
        cursor.close()
        return pdfName.substring(0, pdfName.lastIndexOf("."))
    }

//    private fun extractTextPdfFile(uri: Uri) {
//        try {
//            inputStream = this@MainActivity.contentResolver.openInputStream(uri)
//        } catch (e: FileNotFoundException) {
//            e.printStackTrace()
//        }
//        Thread {
//            val fileContent = StringBuilder()
//            val reader: PdfReader
//            try {
//                reader = PdfReader(inputStream)
//                val pages = reader.numberOfPages
//                for (i in 1..pages) {
//                    fileContent.append(PdfTextExtractor.getTextFromPage(reader, i).trim { it <= ' ' }).append("\n")
//                }
//                reader.close()
//                article!!.textBody = fileContent.toString()
//
//                val dateAdded = dateFormat.format(Date())
//
//                article!!.dateAdded = dateAdded
//                dbHandler!!.addArticle(article)
//
//                val card2add = CardModel(article!!.fileName, article!!.dateAdded)
//                arrayList.add(card2add)
//                myAdapter.notifyDataSetChanged()
//            } catch (e: IOException) {
//                e.printStackTrace()
//            }
//        }.start()
//    }

    private fun extractTextPdfFile(uri: Uri) {
        try {
            inputStream = this@MainActivity.contentResolver.openInputStream(uri)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
        val fileContent = StringBuilder()
        val reader: PdfReader
        try {
            reader = PdfReader(inputStream)
            val pages = reader.numberOfPages
            for (i in 1..pages) {
                fileContent.append(PdfTextExtractor.getTextFromPage(reader, i).trim { it <= ' ' })
                    .append("\n")
            }
            reader.close()
            article!!.textBody = fileContent.toString()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private var pdfResultLauncher = registerForActivityResult(
        StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val data = result.data
            if (data != null) {
                val uri = data.data

                val pdfName = extractPdfName(uri!!)

                article = Article()

                extractTextPdfFile(uri)

                article!!.fileName = pdfName
                val dateAdded = dateFormat.format(Date())

                article!!.dateAdded = dateAdded
                dbHandler!!.addArticle(article)

                val card2add = CardModel(article!!.fileName, article!!.dateAdded)
                arrayList.add(card2add)
                myAdapter.notifyDataSetChanged()
            }
        }
    }

    private var articleResultLauncher = registerForActivityResult(
        StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            if (data != null) {
                val articleName = data.getStringExtra(WebActivity.TITLE)
                val articleBody = data.getStringExtra(WebActivity.BODY)
                val dateAdded = dateFormat.format(Date())
                article = Article(articleName, articleBody, dateAdded)
                dbHandler!!.addArticle(article)

                val card2add = CardModel(article!!.fileName, article!!.dateAdded)
                arrayList.add(card2add)
                myAdapter.notifyDataSetChanged()
            }
        }
    }
}
