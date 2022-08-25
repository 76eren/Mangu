package com.example.mangareader.Activities;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.mangareader.Recyclerviews.chapterlist.*;
import com.example.mangareader.ValueHolders.SourceObjectHolder;
import com.example.mangareader.SourceHandlers.Sources;
import com.example.mangareader.SplashScreen;
import com.example.mangareader.R;
import com.example.mangareader.ValueHolders.ReadValueHolder;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ChaptersActivity extends AppCompatActivity {
    RviewAdapterChapterlist adapter;
    public static String url;
    public static ArrayList<Sources.ValuesForChapters> dataChapters;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_chapters);

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        overridePendingTransition(0, 0);

        // First we retrieve the url
        Intent intent = getIntent();

        String mangaUrl = "";
        String imageUrl = "";
        String mangaName = "";
        try {
            mangaUrl = intent.getStringExtra("url");
            // THE URL TO THE MANGA PAGE; e.g https://readmanganato.com/manga-oa966309
            imageUrl = intent.getStringExtra("img");
            mangaName = intent.getStringExtra("mangaName");
        } catch (Exception ex) {
            Intent x = new Intent(this, HomeActivity.class);
            startActivity(x);
        }
        if (mangaUrl == null || imageUrl == null || mangaName == null) {
            Intent x = new Intent(this, HomeActivity.class);
            startActivity(x);
        }

        TextView Splashscreen = findViewById(R.id.Splashscreen);
        Splashscreen.setText(SplashScreen.returnQuote());

        Activity activity = this;

        String finalMangaUrl = mangaUrl;
        String finalMangaName = mangaName;
        String finalImageUrl = imageUrl;

        new Thread(() -> {
            Sources sources = SourceObjectHolder.getSources(this);
            String mangaStory;

            mangaStory = sources.getStory(finalMangaUrl);
            dataChapters = sources.GetChapters(finalMangaUrl, activity);

            ReadValueHolder.ChaptersActivityData = dataChapters; // LOL imagine assigning values statically lol

            List<ChapterInfo> items = dataChapters.stream()
                    .map(ChapterInfo::new)
                    .collect(Collectors.toList());

            activity.runOnUiThread(() -> {

                RecyclerView recyclerView = findViewById(R.id.rview);
                recyclerView.setLayoutManager(new LinearLayoutManager(activity));

                adapter = new RviewAdapterChapterlist(
                        activity,
                        new HeaderInfo(
                                finalMangaName,
                                finalMangaUrl,
                                finalImageUrl,
                                mangaStory
                        ),
                        items
                );
                recyclerView.setAdapter(adapter);

                Splashscreen.setVisibility(View.INVISIBLE);
            });

        }).start();

    }
}