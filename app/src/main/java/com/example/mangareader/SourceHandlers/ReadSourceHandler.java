package com.example.mangareader.SourceHandlers;

import android.app.Activity;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import com.example.mangareader.Read;
import com.example.mangareader.mangakakalot.Mangakakalot;
import com.github.chrisbanes.photoview.PhotoView;

import java.util.ArrayList;
import java.util.Collections;

public class ReadSourceHandler {
    public void Source(String source, String chapterUrl, Activity activity, Read read, PhotoView photoView, TextView progress) {
        switch (source){
            case "mangakakalot":
                Mangakakalot(chapterUrl, activity,read,photoView,progress);
                break;
        }
    }

    private void Mangakakalot(String chapterUrl, Activity activity, Read read, PhotoView photoView, TextView progress) {
        Mangakakalot mangakakalot = new Mangakakalot();
        ArrayList<String> imgs;
        imgs = mangakakalot.GetImages(chapterUrl);;
        imgs.removeAll(Collections.singleton(null));
        imgs.removeAll(Collections.singleton(""));


        read.AssignData(activity,imgs,photoView,progress); // We assign our context to read

        activity.runOnUiThread(() -> {
            read.ImageShower();
        });
    }

}
