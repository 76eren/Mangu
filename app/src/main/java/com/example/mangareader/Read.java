package com.example.mangareader;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.bumptech.glide.request.RequestOptions;
import com.example.mangareader.SourceHandlers.Sources;
import com.example.mangareader.ValueHolders.ReadValueHolder;
import com.github.chrisbanes.photoview.PhotoView;

import java.util.*;

public class Read{

    private Activity activity;
    public ArrayList<String> images; // Don't leave this empty
    private TextView progress;
    private Sources sources;
    private String currentChapterUrl;
    private TextView CacheStatusUpdate;
    private HashMap<String,String> reqData;

    public PhotoView target; // Make sure to set this to something


    int index = 0;
    private ArrayList<Sources.ValuesForChapters> chaptersData;


    @SuppressLint("SetTextI18n")
    public void LoadGlide() {

        // This does nothing but like its not harming anyone is it?
        // Why remove it then?
        // Help
        if (this.images != null) {
            if (this.images.size() == 0) {
                return;
            }
        }
        else {
            return;
        }


        progress.setText(index+1 +"/"+ (long) this.images.size()+ " - "+ReadValueHolder.currentChapter.name);

        this.activity.runOnUiThread(() -> {
            GlideUrl url;
            if (this.reqData != null) {
                url = new GlideUrl(images.get(index).trim(), new LazyHeaders.Builder()
                        .addHeader("Referer", Objects.requireNonNull(this.reqData.get("Referer")))
                        .build());
            }

            else {
                url = new GlideUrl(images.get(index).trim(), new LazyHeaders.Builder()
                        .build());
            }


            RequestOptions options = new RequestOptions()
                    .diskCacheStrategy(DiskCacheStrategy.ALL);

            Glide.with(activity)
                    .load(url)
                    .timeout(0)
                    .apply(options)
                    .into(target);
        });

    }


    @SuppressLint("SetTextI18n")
    public void Cache() {

        // This function should cache all of the images right before the reading starts
        // This function isn't working correctly right now
        // I am not even sure whether this is the right way to do caching or not
        int index = 1;
        for (String i : this.images) {

            int finalIndex = index;
            activity.runOnUiThread(() -> this.CacheStatusUpdate.setText("Loading image "+ finalIndex +" out of "+this.images.size()));

            Log.d("lol", "HII");
            GlideUrl url = new GlideUrl(i.trim(), new LazyHeaders.Builder()
                    .addHeader("Referer", Objects.requireNonNull(this.reqData.get("Referer")))
                    .build());

            RequestOptions options = new RequestOptions()
                    .diskCacheStrategy(DiskCacheStrategy.ALL);

            Glide.with(activity)
                    //.asBitmap()
                    .load(url)
                    //.timeout(0)
                    .apply(options)
                    .preload();


            index++;
        }

        activity.runOnUiThread(() -> this.CacheStatusUpdate.setVisibility(PhotoView.GONE));
    }




    // I decided to do it through a function because it's less lines of code
    public void AssignData(Context context, ArrayList<String> imgs, PhotoView tgt, TextView pgs, Sources s, HashMap<String,String> reqData) {
        this.activity = (Activity) context;
        this.images=imgs;
        this.target=tgt;
        this.progress = pgs;
        this.sources=s;
        this.currentChapterUrl = ReadValueHolder.currentChapter.url; // THis is a link to the current chapter
        this.reqData=reqData;

        this.CacheStatusUpdate = this.activity.findViewById(R.id.cache);
        this.chaptersData = ReadValueHolder.ChaptersActivityData;

    }

    public void Swipe(int direction) {

        // When loading a chapter if the user immediately tries to swipe, before the images have actually been loaded
        // The app will crash.
        // This should fix that problem.
        if (this.images == null) {
            return;
        }

        if (direction==1) {
            if (index < images.size()-1) {
                index++;
                LoadGlide();
            }
            else {
                try {
                    int len = chaptersData.size();
                    if (!chaptersData.get(len-1).url.equals(this.currentChapterUrl)) {
                        Sources.ValuesForChapters chapter = GetChapter(1); // 1 = next, -1 = previous
                        ReadValueHolder.currentChapter = chapter;
                        LoadChapter(chapter);
                    }

                }
                catch (Exception ex) {
                    Log.d("lol", ex.toString());
                }
            }
        }
        else  {
            if (index>0) {
                index--;
                LoadGlide();
            }
            else {
                // We check whether a previous chapter even exists
                // Before trying to laod it
                if (!this.currentChapterUrl.equals(chaptersData.get(0).url)) {
                    Sources.ValuesForChapters chapterUrl = GetChapter(-1); // 1 = next, -1 = previous

                    ReadValueHolder.currentChapter = chapterUrl;
                    LoadChapter(chapterUrl);
                }
            }

        }

    }

    // gets the url to a chapter
    private Sources.ValuesForChapters GetChapter(int direction) {
        String temp = currentChapterUrl;


        int index = 0;
        for (Sources.ValuesForChapters i : chaptersData) {
            if (temp.equals(i.url)) {
                index = chaptersData.indexOf(i);
                break;
            }
        }
        if (direction == 1) {
            index++;
        }
        else if (direction == -1 && index-1 > -1) {
            index--;
        }

        Sources.ValuesForChapters newChapter = chaptersData.get(index);
        this.currentChapterUrl = newChapter.url;


        return newChapter;

    }


    // I made this function separate so I can call this function
    // From other places as well
    // This gets the images of the new chapter
    public void LoadChapter(Sources.ValuesForChapters object) {
        new Thread(() -> {
            ArrayList<String> imgs = sources.GetImages(object, this.activity);
            imgs.removeAll(Collections.singleton(null));
            imgs.removeAll(Collections.singleton(""));
            // We want to edit a few values
            this.images = imgs;
            this.index=0;

            // Adds our chapter to the history
            ListTracker.AddToList(this.activity, ReadValueHolder.currentChapter.url, "History");


            Settings settings = new Settings();
            if (settings.ReturnValueBoolean(this.activity, "preference_Cache", false)) {
                Cache();
            }

            LoadGlide();



        }).start();
    }


}
