package com.example.mangareader.Downloading;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.preference.PreferenceManager;
import com.example.mangareader.Favourites.FavouriteItem;
import com.example.mangareader.Recyclerviews.chapterlist.ButtonValuesChapterScreen;
import com.example.mangareader.SourceHandlers.Sources;
import com.example.mangareader.ValueHolders.SourceObjectHolder;
import com.google.gson.Gson;

import javax.xml.transform.Source;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

public class DownloadTracker {

    public void removeFromDownloads(CopyOnWriteArrayList<ButtonValuesChapterScreen> values, Context context) {
        // we need to remove these from
        ArrayList<DownloadedChapter> toRemove = new ArrayList<>();
        LinkedHashSet<DownloadedChapter> downloadedChapters =  getFromDownloads(context);

        // We put all the downloads we want to remove in a list
        for (ButtonValuesChapterScreen i : values) {
            for (DownloadedChapter y : downloadedChapters) {
                if (y.getUrl().equals(i.getSelectedButtonUrl())) {
                    toRemove.add(y);
                }
            }
        }

        // We remove the downloads we want to remove from the list
        for (DownloadedChapter i : toRemove) {
            downloadedChapters.remove(i);
        }

        // We convert the objects to jsons again
        // and pull the whole updates downloads list to the sharedpreferences
        Set<String> downloadsJson = new LinkedHashSet<>();
        Gson gson = new Gson();
        for (DownloadedChapter i : downloadedChapters) {
            downloadsJson.add(gson.toJson(i));
        }

        // we push it to the sharedpreferences now
        SharedPreferences sharedpreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putStringSet("Downloads", downloadsJson);
        editor.apply();
    }


    public void addToDownloads(ButtonValuesChapterScreen values, Context context, String[] imageNames, String imagesPath) {
        Gson gson = new Gson();
        DownloadedChapter downloadedChapter = new DownloadedChapter(
                SourceObjectHolder.getSources(context).getClass().getName(),
                values.getValuesForChapters().url,
                (String) values.getExtraData().get("imageUrl"),
                (String) values.getExtraData().get("mangaName"),
                (int) Instant.now().getEpochSecond(),
                (String) values.getExtraData().get("referer"),
                (String) values.getExtraData().get("mangaStory"),
                values.getValuesForChapters().name,
                imageNames,
                imagesPath

        );

        String json = gson.toJson(downloadedChapter);

        SharedPreferences sharedpreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedpreferences.edit();

        LinkedHashSet<String> downloads = new LinkedHashSet<>(
                sharedpreferences.getStringSet("Downloads", new LinkedHashSet<>()));


        downloads.add(json);

        // We push our set to the sharedprefs
        editor.putStringSet("Downloads", downloads);
        editor.apply();


    }

    public LinkedHashSet<DownloadedChapter> getFromDownloads(Context context) {
        SharedPreferences sharedpreferences = PreferenceManager.getDefaultSharedPreferences(context);

        // This is our hashset with Strings
        LinkedHashSet<String> favsSetStrings = new LinkedHashSet<>(
                sharedpreferences.getStringSet("Downloads", new LinkedHashSet<>()));

        // Now we need to convert it to a hashset with Objects
        LinkedHashSet<DownloadedChapter> setObjects = new LinkedHashSet<>();
        Gson gson = new Gson();
        for (String i : favsSetStrings) {
            DownloadedChapter downloadedChapter = gson.fromJson(i, DownloadedChapter.class);
            setObjects.add(downloadedChapter);
        }

        return setObjects;
    }



    public void removeFromDownloads() {

    }


}
