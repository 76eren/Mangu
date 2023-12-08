package com.example.mangareader.Downloading

import android.content.Context
import androidx.preference.PreferenceManager
import com.example.mangareader.SourceHandlers.Sources.ValuesForChapters
import com.example.mangareader.ValueHolders.SourceObjectHolder
import com.google.gson.Gson
import java.time.Instant
import java.util.concurrent.CopyOnWriteArrayList

class DownloadTracker {
    fun removeFromDownloads(values: CopyOnWriteArrayList<ValuesForChapters>, context: Context?) {
        // we need to remove these from
        val toRemove = ArrayList<DownloadedChapter>()
        val downloadedChapters = getFromDownloads(context)

        // We put all the downloads we want to remove in a list
        for (i in values) {
            for (y in downloadedChapters) {
                if (y.url == i.url) {
                    toRemove.add(y)
                }
            }
        }

        // We remove the downloads we want to remove from the list
        for (i in toRemove) {
            downloadedChapters.remove(i)
        }

        // We convert the objects to jsons again
        // and pull the whole updates downloads list to the sharedpreferences
        val downloadsJson: MutableSet<String> = LinkedHashSet()
        val gson = Gson()
        for (i in downloadedChapters) {
            downloadsJson.add(gson.toJson(i))
        }

        // we push it to the sharedpreferences now
        val sharedpreferences = PreferenceManager.getDefaultSharedPreferences(context!!)
        val editor = sharedpreferences.edit()
        editor.putStringSet("Downloads", downloadsJson)
        editor.apply()
    }


    fun addToDownloads(
        values: ValuesForChapters,
        context: Context,
        imageNames: Array<String>,
        imagesPath: String
    ) {
        val gson = Gson()

        @Suppress("UNCHECKED_CAST")
        val downloadedChapter = DownloadedChapter(
            SourceObjectHolder.getSources(context).javaClass.name,
            values.url,
            (values.extraData["imageUrl"] as String),
            (values.extraData["mangaName"] as String),
            Instant.now().epochSecond.toInt(),
            values.extraData["referer"] as String?,
            (values.extraData["mangaStory"] as String),
            values.name,
            imageNames,
            imagesPath,
            values.extraData["chapterNamesDefaultOrder"] as Array<String>
        )

        val json = gson.toJson(downloadedChapter)
        val sharedpreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = sharedpreferences.edit()
        val downloads = LinkedHashSet(
            sharedpreferences.getStringSet("Downloads", LinkedHashSet())
        )
        downloads.add(json)

        // We push our set to the sharedprefs
        editor.putStringSet("Downloads", downloads)
        editor.apply()
    }

    fun getFromDownloads(context: Context?): LinkedHashSet<DownloadedChapter> {
        val sharedpreferences = PreferenceManager.getDefaultSharedPreferences(context!!)

        // This is our hashset with Strings
        val favsSetStrings = LinkedHashSet(
            sharedpreferences.getStringSet("Downloads", LinkedHashSet())
        )

        // Now we need to convert it to a hashset with Objects
        val setObjects = LinkedHashSet<DownloadedChapter>()
        val gson = Gson()
        for (i in favsSetStrings) {
            val downloadedChapter = gson.fromJson(i, DownloadedChapter::class.java)
            setObjects.add(downloadedChapter)
        }
        return setObjects
    }
}