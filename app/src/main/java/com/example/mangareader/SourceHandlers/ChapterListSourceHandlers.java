// THIS CLASS IS FROM GETTING DATA FROM DIFFERENT SOURCES
// FOR THE CHAPTER LIST + STORY


package com.example.mangareader.SourceHandlers;
import com.example.mangareader.mangakakalot.Mangakakalot;

import java.util.LinkedHashMap;

public class ChapterListSourceHandlers {
    public String mangaStory; // Make the functions assign a value to this otherwise our shit will explode
    public LinkedHashMap<String,String> Stalin; // Make the functions assign a value to this otherwise our shit will explode

    public void GetSource(String source, String url) {
        switch (source) {
            case "mangakakalot":
                mangakakalot(url);
                break;
        }
    }

    private void mangakakalot(String chapterUrl) {
        Mangakakalot mangakakalot = new Mangakakalot();
        this.mangaStory = mangakakalot.getStory(chapterUrl); // We might want to trim this?????
        // We now start scraping all of the chapters
        this.Stalin = mangakakalot.GetChapters(chapterUrl);

    }


}
