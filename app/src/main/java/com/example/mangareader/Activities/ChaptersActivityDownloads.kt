package com.example.mangareader.Activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mangareader.Downloading.DownloadTracker
import com.example.mangareader.Downloading.DownloadedChapter
import com.example.mangareader.Downloading.RelevantDownloadsSorter
import com.example.mangareader.GenerateExtraData
import com.example.mangareader.R
import com.example.mangareader.Recyclerviews.chapterlist.ChapterInfo
import com.example.mangareader.Recyclerviews.chapterlist.HeaderInfo
import com.example.mangareader.Recyclerviews.chapterlist.RviewAdapterChapterlist
import com.example.mangareader.SourceHandlers.Sources.ValuesForChapters
import com.example.mangareader.SplashScreen
import com.example.mangareader.ValueHolders.ReadValueHolder

class ChaptersActivityDownloads : AppCompatActivity() {
    var adapter: RviewAdapterChapterlist? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chapters_downloads)

        // TODO: Make the activity_chapters_downloads and activity_chapters read the same layout file instead of copying the same layout twice
        // TODO: Fix toolbar for this activity

        supportActionBar!!.setDisplayShowTitleEnabled(true)
        overridePendingTransition(0, 0)

        Log.d("lol", "kekw")

        val intent = intent
        var mangaUrl = ""
        var imageUrl = ""
        var mangaName = ""
        var referer: String? = null

        try {
            // Make sure these are not null
            mangaUrl = intent.getStringExtra("url")!!
            imageUrl = intent.getStringExtra("img")!!
            mangaName = intent.getStringExtra("mangaName")!!
            referer = intent.getStringExtra("referer")
        }
        catch (ex: Exception) {
            val x = Intent(this, HomeActivity::class.java)
            startActivity(x)
        }

        val Splashscreen = findViewById<TextView>(R.id.Splashscreen)
        Splashscreen.text = SplashScreen.returnQuote()

        val relevantDownloads = ArrayList<DownloadedChapter>()

        val downloadTracker = DownloadTracker()
        val downloads = downloadTracker.getFromDownloads(this)
        for (i in downloads) {
            if (i.name == mangaName) {
                relevantDownloads.add(i)
            }
        }

        val downloadSorter = RelevantDownloadsSorter()

        try {
            val sorted: ArrayList<DownloadedChapter> = downloadSorter.sortingOptionOne(relevantDownloads)
            relevantDownloads.clear()
            relevantDownloads.addAll(sorted)
        }
        catch (ex: java.lang.Exception) {
            try {
                val sorted: ArrayList<DownloadedChapter> = downloadSorter.sortingOptionTwo(relevantDownloads)
                relevantDownloads.clear()
                relevantDownloads.addAll(sorted)
            }
            catch (ignored : java.lang.Exception) { }
        }

        val activity: Activity = this
        Thread {
            val mangaStory: String

            // The mangastory is for every object the same, so we can just get the first object in our arraylist
            mangaStory = relevantDownloads[0].mangaStory

            // Now we generate the extra data
            val generator = GenerateExtraData()
            val extraData = generator.generateExtraData(mangaUrl, imageUrl, mangaName, referer, mangaStory)

            val dataChapters = ArrayList<ValuesForChapters>()

            for (i in relevantDownloads) {
                val valuesForChapters = ValuesForChapters()
                valuesForChapters.name = i.chapterName
                valuesForChapters.url = i.url
                valuesForChapters.extraData = extraData

                var canAdd = true
                for (y in dataChapters) {
                    if (y.url == valuesForChapters.url) {
                        canAdd = false
                        break
                    }
                }
                if (canAdd) {
                    dataChapters.add(valuesForChapters)
                }
            }

            ReadValueHolder.ChaptersActivityData = dataChapters // LOL imagine assigning values statically lol

            val items: MutableList<ChapterInfo> = java.util.ArrayList()
            for (chapterData in dataChapters) {
                val chapterInfo = ChapterInfo(chapterData, extraData, activity, true)
                items.add(chapterInfo)
            }

            activity.runOnUiThread {
                val recyclerView = findViewById<RecyclerView>(R.id.rviewChapters)
                recyclerView.layoutManager = LinearLayoutManager(activity)
                adapter = RviewAdapterChapterlist(
                    activity,
                    HeaderInfo(
                        mangaName,
                        mangaUrl,
                        imageUrl,
                        mangaStory,
                        referer,  // may be null
                        extraData,
                        true
                    ),
                    items
                )
                recyclerView.adapter = adapter
                Splashscreen.visibility = View.INVISIBLE
            }


        }.start()


    }




}