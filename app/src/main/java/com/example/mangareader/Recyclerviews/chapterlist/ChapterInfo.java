package com.example.mangareader.Recyclerviews.chapterlist;

import android.app.Activity;
import com.example.mangareader.SourceHandlers.Sources;

import java.util.HashMap;

public class ChapterInfo {
    private final Sources.ValuesForChapters valuesForChapters;
    private final boolean isDownloaded;
    private ChapterListButton chapterListButton;
    public Activity activity;


    // We want to be able to access this extraData at all times
    // For now it is being used for the download feature but in the future if I ever add a new feature this HashMap might prove usefull.
    private final HashMap<String, Object> extraData;

    public ChapterInfo(Sources.ValuesForChapters valuesForChapters, HashMap<String, Object> extraData, Activity activity, boolean isDownloaded) {
        this.valuesForChapters = valuesForChapters;
        this.extraData = extraData;
        this.activity = activity;
        this.isDownloaded = isDownloaded;
    }

    public HashMap<String, Object> getExtraData() {
        return extraData;
    }

    public Sources.ValuesForChapters getValuesForChapters() {
        return this.valuesForChapters;
    }

    public void setChapterListButton(ChapterListButton chapterListButton) {
        this.chapterListButton = chapterListButton;
    }

    public ChapterListButton getChapterListButton() {
        return chapterListButton;
    }

    public boolean isDownloaded() {
        return isDownloaded;
    }
}
