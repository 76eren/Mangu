package com.example.mangareader.Activities;

import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.bumptech.glide.MemoryCategory;
import com.example.mangareader.R;
import com.example.mangareader.Read;
import com.example.mangareader.SourceHandlers.ObjectHolder;
import com.example.mangareader.SourceHandlers.Sources;
import com.github.chrisbanes.photoview.PhotoView;

import java.util.ArrayList;
import java.util.Collections;

public class ReadActivity extends AppCompatActivity {

    private final Read read = new Read();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read);
        PhotoView photoView = findViewById(R.id.photo_view);
        TextView progress = findViewById(R.id.progress);

        // we set up our temp clickers
        // I'll replace this with swiping once I figure out how to
        // Make it work together with PhotoView
        Button prevClick = findViewById(R.id.prevPage);
        prevClick.setBackgroundColor(Color.TRANSPARENT);
        float x = 0;
        prevClick.setAlpha(x);
        Button nextClick = findViewById(R.id.nextPage);
        nextClick.setBackgroundColor(Color.TRANSPARENT);
        nextClick.setAlpha(x);

        prevClick.setOnClickListener(v -> read.Swipe(-1));

        nextClick.setOnClickListener(v -> read.Swipe(1));


        Intent intent = getIntent();
        String chapterUrl = intent.getStringExtra("url");
        Log.d("lol", chapterUrl);


        new Thread(() -> {
            Sources source;
            source = ObjectHolder.sources; // We assigned this in the main activity

            ArrayList<String> imgs;
            imgs = source.GetImages(chapterUrl);


            // If this triggers it means you fucked up
            // And something went wrong
            // We'll just load the mainactivity witouth notifying the user
            // whatsoever because that makes sense
            if (imgs == null) {
                Log.d("lol", "An error occured whilst trying to load the list with chapters");
                Intent d = new Intent(this, MainActivity.class);
                startActivity(d);
            }

            imgs.removeAll(Collections.singleton(null));
            imgs.removeAll(Collections.singleton(""));

            read.AssignData(this,imgs,photoView,progress,source); // We assign our context to read


            // Right now caching is pretty forced. Maybe make it not forced and add a setting to disable it?
            read.Cache();


            read.ImageShower();




        }).start();

    }









}