package com.example.mangareader.Activities;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.mangareader.Recyclerviews.RviewAdapterChapterlist;
import com.example.mangareader.ValueHolders.ObjectHolder;
import com.example.mangareader.SourceHandlers.Sources;
import com.example.mangareader.SplashScreen;
import com.example.mangareader.R; // Not importing this will break the whole thingy lol
import com.example.mangareader.ValueHolders.ReadValueHolder;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class ChaptersActivity extends AppCompatActivity {
    RviewAdapterChapterlist adapter;
    public static String url;
    public static ArrayList<Sources.ValuesForChapters> dataChapters;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        this.setContentView(R.layout.activity_chapters);

        // First we retrieve the url
        Intent intent = getIntent();
        String mangaUrl = intent.getStringExtra("url"); // THE URL TO THE MANGA PAGE; e.g https://readmanganato.com/manga-oa966309
        String imageUrl = intent.getStringExtra("img");

        TextView Splashscreen = findViewById(R.id.Splashscreen);
        Splashscreen.setText(SplashScreen.returnQuote());

        Activity activity = this;

        new Thread(() -> {
            Sources sources = ObjectHolder.sources;
            String mangaStory;

            mangaStory = sources.getStory(mangaUrl);
            dataChapters = sources.GetChapters(mangaUrl);


            ReadValueHolder.ChaptersActivityData = dataChapters; // LOL imagine assigning values statically lol


            List<RviewAdapterChapterlist.Data> data = new ArrayList<>();
            data.add(new RviewAdapterChapterlist.Data("the_fucking_star","", null, mangaUrl+"_"+imageUrl, this));
            data.add(new RviewAdapterChapterlist.Data(imageUrl,"",null, "poster", this));
            data.add(new RviewAdapterChapterlist.Data("",mangaStory,null, "Clickable", this));

            for (Sources.ValuesForChapters i : dataChapters) {
                url = i.url;
                data.add(new RviewAdapterChapterlist.Data("","", i, url, this));
            }

            activity.runOnUiThread(() -> {
                RecyclerView recyclerView = findViewById(R.id.rview);
                recyclerView.setLayoutManager(new LinearLayoutManager(activity));
                adapter = new RviewAdapterChapterlist(activity, data, "imageview");
                recyclerView.setAdapter(adapter);

                Splashscreen.setVisibility(View.INVISIBLE);
            });



        }).start();

    }
}