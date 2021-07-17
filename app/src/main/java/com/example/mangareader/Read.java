package com.example.mangareader;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.MemoryCategory;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.bumptech.glide.request.RequestOptions;
import com.example.mangareader.SourceHandlers.Sources;
import com.github.chrisbanes.photoview.PhotoView;

import java.util.*;

public class Read{

    // I made this class in such a way
    // That it'll also work with other sources

    private Context context;
    private Activity activity;
    public ArrayList<String> images; // Don't leave this fuckign empty
    private TextView progess;
    private Sources sources;
    private String currentChapter;
    private TextView CacheStatusUpdate;

    public PhotoView target; // Make sure to set this to something
    public TextView progress;


    int index = 0;
    public static LinkedHashMap<String,String> chaptersXurls;

    @SuppressLint("SetTextI18n")
    public void ImageShower() {
        Log.d("lol", "Starting");
        progess.setText(index+1 +"/"+ (long) this.images.size());

        this.activity.runOnUiThread(() -> {
            GlideUrl url = new GlideUrl(images.get(index).trim(), new LazyHeaders.Builder()
                    .addHeader("User-Agent", "Mozilla/5.0 (X11; Linux x86_64; rv:89.0) Gecko/20100101 Firefox/89.0")
                    .addHeader("Referer", "https://mangakakalot.com/")
                    .build());

            RequestOptions options = new RequestOptions()
                    .timeout(30)
                    .diskCacheStrategy(DiskCacheStrategy.ALL);

            Glide.with(activity)
                    .load(url)
                    .into(target);
        });

    }

    @SuppressLint({"CheckResult", "SetTextI18n"})

    public void Cache() {
        // This function should cache all of the images right before the reading starts
        // This function isn't working correctly right now
        // I am not even sure whether this is the right way to do caching or not
        int index = 1;
        for (String i : this.images) {

            int finalIndex = index;
            activity.runOnUiThread(() -> {
                this.CacheStatusUpdate.setText("Loading image "+String.valueOf(finalIndex)+" out of "+this.images.size());
            });

            Log.d("lol", "HII");
            GlideUrl url = new GlideUrl(i.trim(), new LazyHeaders.Builder()
                    .addHeader("User-Agent", "Mozilla/5.0 (X11; Linux x86_64; rv:89.0) Gecko/20100101 Firefox/89.0")
                    .addHeader("Referer", "https://mangakakalot.com/")
                    .build());

            RequestOptions options = new RequestOptions()
                    .timeout(1000)
                    .diskCacheStrategy(DiskCacheStrategy.ALL);

            Glide.with(activity)
                    .load(url);    // We don't want to load into anything. We just want to load

            index++;
        }

        activity.runOnUiThread(() -> this.CacheStatusUpdate.setVisibility(PhotoView.GONE));

    }


    public void Swipe(int direction) {
        if (direction==1) {
            if (index < images.size()-1) {
                index++;
                ImageShower();
            }
            else {
                try {
                    // We go to the next chapter
                    // Bug first we have to check whether a next chapter even exists

                    Object[] x = chaptersXurls.keySet().toArray();
                    long p = Arrays.stream(x).count();
                    int i = (int)p;
                    String chapt = x[i-1].toString();

                    if (chapt != this.currentChapter) {
                        LoadChapter(1); // 1 = next, -1 = previous
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
                ImageShower();
            }
            else {
                // We check whether a previous chapter even exists
                // Before trying to laod it
                if (this.currentChapter != chaptersXurls.keySet().toArray()[0]) {
                    LoadChapter(-1);
                }
            }

        }

    }



    // I decided to do it throught a function because it's less lines of code
    public void AssignData(Context context, ArrayList imgs, PhotoView tgt, TextView pgs, Sources s) {
        this.context=context;
        this.activity = (Activity) context;
        this.images=imgs;
        this.target=tgt;
        this.progess = pgs;
        this.sources=s;
        this.currentChapter = activity.getIntent().getStringExtra("chapter");

        this.CacheStatusUpdate = this.activity.findViewById(R.id.cache);
    }

    // This function is in charge of loading the next/previous chapter
    private void LoadChapter(int direction) {
        String chapter = currentChapter;
        List<String> chapters = new ArrayList<>(this.chaptersXurls.keySet());
        int index = chapters.indexOf(chapter);

        // Can still throw an index out of bounds exception
        // please check for that first
        // Don't forget EREN
        if (direction == 1) {
            index++; // We want to go to the next chapter of course
        }
        else if (direction == -1) {
            index--;
        }
        String newChapter = chapters.get(index);
        this.currentChapter = newChapter;

        String newUrl = this.chaptersXurls.get(newChapter);
        Log.d("lol", newUrl);

        new Thread(() -> {
            ArrayList<String> imgs = sources.GetImages(newUrl);
            imgs.removeAll(Collections.singleton(null));
            imgs.removeAll(Collections.singleton(""));
            // We want to edit a few values
            this.images = imgs;
            this.index=0;
            Cache();
            ImageShower();
        }).start();
    }










}
