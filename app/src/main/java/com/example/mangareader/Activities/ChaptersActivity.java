package com.example.mangareader.Activities;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
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
import org.json.JSONException;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
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
        String referer = null;
        try {
            mangaUrl = intent.getStringExtra("url");
            // THE URL TO THE MANGA PAGE; e.g https://readmanganato.com/manga-oa966309
            imageUrl = intent.getStringExtra("img");
            mangaName = intent.getStringExtra("mangaName");
            referer = intent.getStringExtra("referer");
        }
        catch (Exception ex) {
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
        String finalReferer = referer;

        String finalMangaName1 = mangaName;
        new Thread(() -> {
            Sources sources = SourceObjectHolder.getSources(this);
            String mangaStory;

            mangaStory = sources.getStory(finalMangaUrl);


            // So this is probably very unnecessary... for now
            // I made this hashmap in case we ever come across a situation where we need any of these values
            // e.g for now the only value being used is the mangaName, this is being used by the Webtoons source
            // I don't really like the way this works too much but it is what it is.
            HashMap<String, Object> extraData = new HashMap<>();
            extraData.put("mangaName", finalMangaName1);
            extraData.put("imageUrl", finalImageUrl);
            extraData.put("referer", finalReferer);
            extraData.put("mangaUrl", finalMangaUrl);

            try {
                dataChapters = sources.GetChapters(finalMangaUrl, activity, extraData);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            } catch (InvalidKeyException e) {
                throw new RuntimeException(e);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }

            // We just want to restart if this is null, so we don't run into errors later on
            if (dataChapters == null) {
                Intent x = new Intent(this, HomeActivity.class);
                startActivity(x);
            }

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
                                mangaStory,
                                finalReferer // may be null
                        ),
                        items
                );
                recyclerView.setAdapter(adapter);

                Splashscreen.setVisibility(View.INVISIBLE);
            });

        }).start();

    }
}