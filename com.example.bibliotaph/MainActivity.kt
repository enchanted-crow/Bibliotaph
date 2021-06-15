package com.example.bibliotaph

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
import com.example.bibliotaph.adapters.MyAdapter
import com.example.bibliotaph.models.Article
import com.example.bibliotaph.models.CardModel
import com.example.bibliotaph.params.AppGlobals.cardList
import com.example.bibliotaph.params.AppGlobals.myDB
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.itextpdf.text.pdf.PdfReader
import com.itextpdf.text.pdf.parser.PdfTextExtractor
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity(), MyAdapter.OnCardListener {
//    <Jawad>

//    view-related variables
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

//    state variables
    private var addButtonExpanded : Boolean = false

//    private var cardList = ArrayList<CardModel>(1000)
    private lateinit var myAdapter : MyAdapter
    private var linearLayoutManager : LinearLayoutManager? = null
//    </Jawad>



//    <Tahmid>
    private var inputStream: InputStream? = null
//    private var myDB: DbHandler? = null
    private var article2add: Article? = null
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
//    </Tahmid>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initAddButton()

        myDB = DbHandler(this);
        cardList = createCardListFromDB(myDB!!)

        linearLayoutManager = LinearLayoutManager(this)
        setupRecyclerView(linearLayoutManager!!)
    }

    override fun onCardClick(position: Int) {
        val intent = Intent(this, ReadingScreenActivity::class.java)
        intent.putExtra("cardArrListIndex", position)
        startActivity(intent)
    }

    private fun createCardListFromDB(myDB: DbHandler) : ArrayList <CardModel> {
        val articleList = myDB.allArticles;
        val cardList = ArrayList <CardModel> (1000)
        for (article in articleList) {
            val card = CardModel(article.fileName, article.dateAdded)
            cardList.add(card);
        }
        return cardList
    }

    private fun setupRecyclerView(layoutManager: RecyclerView.LayoutManager) {
        recyclerView = findViewById(R.id.recycler_view)

        myAdapter = MyAdapter(cardList, this)
        recyclerView.adapter = myAdapter

        recyclerView.layoutManager = layoutManager
    }

    private fun initAddButton() {
        addButton = findViewById(R.id.add_pdf_nd_article_button)
        addButton.setOnClickListener {
            onClickAddButton(addButtonExpanded)
        }

        addPDFButton = findViewById(R.id.add_pdf_button)
        addPDFButton.setOnClickListener {
            Toast.makeText(this, "PDF!", Toast.LENGTH_SHORT).show()
            callChooseFileFromDevice()
        }

        addArticleButton = findViewById(R.id.add_article_button)
        addArticleButton.setOnClickListener {
            Toast.makeText(this, "Article!", Toast.LENGTH_SHORT).show()
            callChooseArticleFromWeb()
        }
    }

    private fun onClickAddButton(function: Boolean) {
        setVisibility(addButtonExpanded)
        setAnimation(addButtonExpanded)
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


    // tahmid's methods

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
            article2add!!.textBody = fileContent.toString()
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

                article2add = Article()

                extractTextPdfFile(uri)

                article2add!!.fileName = pdfName
                val dateAdded = dateFormat.format(Date())

                article2add!!.dateAdded = dateAdded
                myDB!!.addArticle(article2add)

                val card2add = CardModel(article2add!!.fileName, article2add!!.dateAdded)
                cardList.add(card2add)
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
                article2add = Article(articleName, articleBody, dateAdded)
                myDB!!.addArticle(article2add)

                val card2add = CardModel(article2add!!.fileName, article2add!!.dateAdded)
                cardList.add(card2add)
                myAdapter.notifyDataSetChanged()
            }
        }
    }
}
