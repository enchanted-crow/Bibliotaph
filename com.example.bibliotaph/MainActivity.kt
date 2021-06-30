package com.example.bibliotaph

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bibliotaph.adapters.MyAdapter
import com.example.bibliotaph.models.Article
import com.example.bibliotaph.models.CardModel
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity(), MyAdapter.OnCardListener {
//    <Jawad>

    //    view-related variables
    private lateinit var recyclerView: RecyclerView
    private lateinit var addButton : FloatingActionButton
    private lateinit var addPDFButton : FloatingActionButton
    private lateinit var addArticleButton : FloatingActionButton
    private lateinit var playRecentButton : FloatingActionButton

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
    private var toolbar : androidx.appcompat.widget.Toolbar? = null
    private lateinit var bottomAppBar : BottomAppBar
//    private lateinit var recentlyPlayedFileName : String
//    private var recentlyPlayedCurrentSentence = 0
    private var recentlyPlayedFileName = "Article Name"

    private lateinit var myAdapter : MyAdapter
    private var linearLayoutManager : LinearLayoutManager? = null
//    </Jawad>


    //    <Tahmid>
    companion object {
//        const val POSITION : String = "com.example.bibliotaph.INDEX"
//        lateinit var articleList : ArrayList<Article>
        const val TITLE : String = "com.example.bibliotaph.TITLE"
        const val SOURCE : String = "com.example.bibliotaph.SOURCE"
        const val PLAY : String = "com.example.bibliotaph.PLAY"
        lateinit var dbHandler : DbHandler
    }

    private var sortIndex: Int = 0
    private var cardList = ArrayList<CardModel>(1000)
    private var article2add: Article? = null
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
//    </Tahmid>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        dbHandler = DbHandler(this)
        createCardListFromDB()

        initAddButton()
        initToolbar()
        loadRecentlyPlayedData()

        linearLayoutManager = LinearLayoutManager(this)
        setupRecyclerView(linearLayoutManager!!)
    }

    private fun createCardListFromDB() {
//        articleList = dbHandler.getAllArticles(sortIndex)
        cardList.clear()
//        for (article in articleList) {
//            val card = CardModel(article.fileName, article.dateAdded)
//            cardList.add(card)
//        }
        cardList.addAll(dbHandler.getAllArticles(sortIndex))
//        cardList = dbHandler.getAllArticles(sortIndex)
    }

    private fun setupRecyclerView(layoutManager: RecyclerView.LayoutManager) {
        recyclerView = findViewById(R.id.recycler_view)

        myAdapter = MyAdapter(cardList, this)
        recyclerView.adapter = myAdapter

        recyclerView.layoutManager = layoutManager
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.settings -> {
                Toast.makeText(this, "Settings", Toast.LENGTH_SHORT).show()
                val intent = Intent(this@MainActivity, SettingsActivity::class.java)
                startActivity(intent)
            }
            R.id.about -> {
                Toast.makeText(this, "About", Toast.LENGTH_SHORT).show()
            }
            R.id.search -> {
                Toast.makeText(this, "Search", Toast.LENGTH_SHORT).show()
            }
            R.id.sort_by_date -> {
                Toast.makeText(this, "Sort by date", Toast.LENGTH_SHORT).show()
                sortIndex = 0
                createCardListFromDB()
                myAdapter.notifyDataSetChanged()
            }
            R.id.sort_by_alphabet -> {
                Toast.makeText(this, "Sort by name", Toast.LENGTH_SHORT).show()
                sortIndex = 1
                createCardListFromDB()
                myAdapter.notifyDataSetChanged()
            }
        }
        return true
    }

    private fun initToolbar() {
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        bottomAppBar = findViewById(R.id.bottomAppBar)
        bottomAppBar.title = recentlyPlayedFileName
        bottomAppBar.setOnClickListener {
            val intent = Intent(this, ReadingScreenActivity::class.java)
            intent.putExtra(PLAY, false)
            startActivity(intent)
        }
    }

    private fun initAddButton() {
        addButton = findViewById(R.id.add_pdf_nd_article_button)
        addButton.setOnClickListener {
            onClickAddButton()
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

        playRecentButton = findViewById(R.id.play_recent_button)
        playRecentButton.setOnClickListener {
            val intent = Intent(this, ReadingScreenActivity::class.java)
            intent.putExtra(PLAY, true)
            startActivity(intent)
        }

    }

    private fun onClickAddButton() {
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

    override fun onCardClick(position: Int) {
        val intent = Intent(this, ReadingScreenActivity::class.java)
//        intent.putExtra(POSITION, position)
        intent.putExtra(TITLE, cardList[position].fileName)
        intent.putExtra(SOURCE, 1)
        startActivity(intent)
    }

    override fun onCardLongClick(position: Int, view : View?) {
        showRvPopupMenu(position, view)
//        Toast.makeText(this, "long click at $position", Toast.LENGTH_SHORT).show()
    }

    private fun showRvPopupMenu(position: Int, view : View?) {
        val popupMenu = PopupMenu(view?.context, view)
        popupMenu.inflate((R.menu.rec_v_longclick_menu))

        popupMenu.setOnMenuItemClickListener {
            if (it != null) {
                when(it.itemId) {
                    R.id.delete -> {
                        val fileName = cardList[position].fileName
                        dbHandler.deleteArticle(fileName)
                        cardList.removeAt(position)
                        myAdapter.notifyDataSetChanged()
                        Toast.makeText(this, "delete at $position", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            return@setOnMenuItemClickListener false
        }
        popupMenu.show()
    }

    private fun loadRecentlyPlayedData() {
        val sharedPreferences = getSharedPreferences(ReadingScreenActivity.SHARED_PREFS, MODE_PRIVATE)

        recentlyPlayedFileName = sharedPreferences.getString(ReadingScreenActivity.ARTICLE_NAME, "Article Name")!!
//        recentlyPlayedCurrentSentence = sharedPreferences.getInt(ReadingScreenActivity.CURRENT_SENTENCE, 0)
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

    private var pdfResultLauncher = registerForActivityResult(
        StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val data = result.data
            if (data != null) {
                val loadingDialog = LoadingDialog(this)
                loadingDialog.startLoadingDialog()
                Thread {
                    val uri = data.data

                    val pdfName = PdfProcessor.extractPdfName(this, uri!!)
                    val pdfBody = PdfProcessor.extractTextPdfFile(this, uri)
                    val dateAdded = dateFormat.format(Date())

                    article2add = Article(pdfName, pdfBody, dateAdded)
                    dbHandler.addArticle(article2add)
                    createCardListFromDB()
                    runOnUiThread {
                        myAdapter.notifyDataSetChanged()
                    }
                    loadingDialog.dismissDialog()
                }.start()
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
                dbHandler.addArticle(article2add)
                createCardListFromDB()
                myAdapter.notifyDataSetChanged()
            }
        }
    }
}
