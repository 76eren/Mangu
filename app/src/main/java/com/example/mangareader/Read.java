/*
/ Requirements for this class
/   - Everything necessary for the AssignData function
/   - The ChapterxUrl linkedhashmap in the activity's intent (the one that gets passed throught the AssignData function)
/   - The chapter name in the activity's intent (again see comment above)
/   -
/   -
/
*/



package com.example.mangareader;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.bumptech.glide.request.RequestOptions;
import com.example.mangareader.mangakakalot.Activities.ReadActivity;
import com.github.chrisbanes.photoview.PhotoView;

import java.util.*;
import java.util.stream.Collectors;

public class Read{

    // I made this class in such a way
    // That it'll also work with other sources

    private Context context;
    private Activity activity;
    public ArrayList<String> images; // Don't leave this fuckign empty
    private TextView progess;

    public PhotoView target; // Make sure to set this to something
    public TextView progress;

    int index = 0;
    public static LinkedHashMap<String,String> chaptersXurls;

    public void ImageShower() {
        Log.d("lol", "calling fun");

        progess.setText(index+1 +"/"+this.images.stream().count()); // This might be brtoken???
        GlideUrl url = new GlideUrl(images.get(index), new LazyHeaders.Builder()
                .addHeader("User-Agent", "Mozilla/5.0 (X11; Linux x86_64; rv:89.0) Gecko/20100101 Firefox/89.0")
                .addHeader("Referer", "https://mangakakalot.com/")
                .build());

        RequestOptions options = new RequestOptions()
                .timeout(30)
                .diskCacheStrategy(DiskCacheStrategy.ALL);

        Glide.with(activity)
                .load(url)
                .into(target);


    }


    public void Swipe(int direction) {

        if (direction==1) {
            if (index < images.size()-1) {
                index++;
                ImageShower();
            }
            else {
                // We somehow load the next chapter
                try {

                    String chapter = activity.getIntent().getStringExtra("chapter");
                    List<String> chapters = new ArrayList<>(this.chaptersXurls.keySet());
                    int index = chapters.indexOf(chapter);
                    index++; // We want to go to the next chapter of course
                    String newChapter = chapters.get(index);
                    String newUrl = this.chaptersXurls.get(newChapter);

                    // Now we just restart the fucking activity
                    // because I'm lazy

                    Intent intent = new Intent(activity, activity.getClass());
                    intent.putExtra("url", newUrl);
                    intent.putExtra("chapter", newChapter);
                    activity.startActivity(intent);
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

        }

    }

    // I decided to do it throught a function because it's less lines of code
    public void AssignData(Context context, ArrayList imgs, PhotoView tgt, TextView pgs) {
        this.context=context;
        this.activity = (Activity) context;
        this.images=imgs;
        this.target=tgt;
        this.progess = pgs;



    }










}
