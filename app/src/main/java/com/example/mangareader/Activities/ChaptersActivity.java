package com.example.mangareader.Activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.mangareader.Downloading.Downloader;
import com.example.mangareader.Favourites.FavouriteItem;
import com.example.mangareader.Favourites.Favourites;
import com.example.mangareader.GenerateExtraData;
import com.example.mangareader.R;
import com.example.mangareader.Recyclerviews.chapterlist.ChapterInfo;
import com.example.mangareader.Recyclerviews.chapterlist.ChapterListButton;
import com.example.mangareader.Recyclerviews.chapterlist.HeaderInfo;
import com.example.mangareader.Recyclerviews.chapterlist.RviewAdapterChapterlist;
import com.example.mangareader.Settings.ListTracker;
import com.example.mangareader.SourceHandlers.Sources;
import com.example.mangareader.SplashScreen;
import com.example.mangareader.ValueHolders.ReadValueHolder;
import com.example.mangareader.ValueHolders.SourceObjectHolder;
import org.json.JSONException;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class ChaptersActivity extends AppCompatActivity {
    public ArrayList<Sources.ValuesForChapters> dataChapters = new ArrayList<>();
    RviewAdapterChapterlist adapter;
    ArrayList<ChapterInfo> items = new ArrayList<>();
    private HashMap<String, Object> extraData = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_chapters_view);

        overridePendingTransition(0, 0);

        // First we retrieve the url
        Intent intent = getIntent();

        String mangaUrl = "";
        String imageUrl = "";
        String mangaName = "";
        String referer = null;

        try {
            // Make sure these are not null
            mangaUrl = intent.getStringExtra("url");
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

        String finalMangaUrl = mangaUrl;
        String finalMangaName = mangaName;
        String finalImageUrl = imageUrl;
        String finalReferer = referer;
        String finalMangaName1 = mangaName;

        Activity activity = this;
        new Thread(() -> {
            Sources sources = SourceObjectHolder.getSources(this);
            String mangaStory;
            mangaStory = sources.getStory(finalMangaUrl);


            // Just a bunch of variables we constantly want to track
            GenerateExtraData generateExtraData = new GenerateExtraData();
            this.extraData = generateExtraData.generateExtraData(finalMangaUrl, finalImageUrl, finalMangaName1, finalReferer, mangaStory);

            try {
                dataChapters = sources.getChapters(finalMangaUrl, activity, this.extraData);

                ArrayList<String> chapterNamesDefaultOrderArraylist = new ArrayList<>();
                for (Sources.ValuesForChapters i : dataChapters) {
                    chapterNamesDefaultOrderArraylist.add(i.name);
                }
                String[] chapterNamesDefaultOrder = chapterNamesDefaultOrderArraylist.toArray(new String[0]);
                generateExtraData.addChapterNamesDefaultOrder(this.extraData, chapterNamesDefaultOrder);

            } catch (IOException | NoSuchAlgorithmException | InvalidKeyException | JSONException e) {
                throw new RuntimeException(e);
            }

            // We just want to restart if this is null, so we don't run into errors later on
            if (dataChapters == null) {
                Intent x = new Intent(this, HomeActivity.class);
                startActivity(x);
            }


            ReadValueHolder.ChaptersActivityData = dataChapters; // LOL imagine assigning values statically lol

            for (Sources.ValuesForChapters chapterData : dataChapters) {
                ChapterInfo chapterInfo = new ChapterInfo(chapterData, extraData, activity, false);
                this.items.add(chapterInfo);
            }

            activity.runOnUiThread(() -> {
                RecyclerView recyclerView = findViewById(R.id.rviewChapters);
                recyclerView.setLayoutManager(new LinearLayoutManager(activity));
                adapter = new RviewAdapterChapterlist(
                        activity,
                        new HeaderInfo(
                                finalMangaName,
                                finalMangaUrl,
                                finalImageUrl,
                                mangaStory,
                                finalReferer, // may be null
                                extraData,
                                false
                        ),
                        this.items
                );
                recyclerView.setAdapter(adapter);
                Splashscreen.setVisibility(View.INVISIBLE);
            });

        }).start();

    }


    private void resetButtons() {
        for (ChapterInfo chapterInfo : this.items) {
            ChapterListButton chapterListButton = chapterInfo.getChapterListButton();
            if (chapterListButton == null) {
                continue;
            }
            if (chapterListButton.enabledButtons == null) {
                continue;
            }

            for (Button i : chapterListButton.enabledButtons) {
                // Put back the default colour
                i.setTextColor(ChapterListButton.getButtonColor(chapterInfo.getValuesForChapters().url, this));

            }
            chapterListButton.enabledButtons.clear();
            chapterListButton.valuesForChaptersList.clear();

        }
        ChapterListButton.staticShouldEnableToolbar = false;
    }


    @Override
    public void onBackPressed() {
        if (ChapterListButton.staticShouldEnableToolbar) {
            resetButtons();
        }
        else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.chaptersactivity_toolbar_menu, menu);
        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.chaptersactivity_action_toolbar_download:
                ArrayList<Sources.ValuesForChapters> thingsToDownload = new ArrayList<>();

                for (ChapterInfo chapterInfo : this.items) {
                    ChapterListButton chapterListButton = chapterInfo.getChapterListButton();
                    if (chapterListButton == null) {
                        continue;
                    }
                    if (chapterListButton.enabledButtons == null) {
                        continue;
                    }
                    thingsToDownload.addAll(chapterListButton.valuesForChaptersList);
                }
                resetButtons();

                for (Sources.ValuesForChapters i : thingsToDownload) {
                    i.extraData = this.extraData;
                    i.activity = this;
                }

                Downloader downloader = new Downloader();
                downloader.download(thingsToDownload, this);


                return true;

            case R.id.chaptersactivity_action_toolbar_read_unread:
                for (ChapterInfo chapterInfo : this.items) {
                    ChapterListButton chapterListButton = chapterInfo.getChapterListButton();
                    if (chapterListButton == null) {
                        continue;
                    }
                    if (chapterListButton.enabledButtons == null) {
                        continue;
                    }

                    for (Sources.ValuesForChapters i : chapterListButton.valuesForChaptersList) {
                        ListTracker.changeStatus(this, i.url, "History");
                    }
                }
                resetButtons();
                return true;

            case R.id.chaptersactivity_action_toolbar_favourite:
                Object referer = extraData.get("referer");
                String refererString = null;
                if (referer != null) {
                    refererString = referer.toString();
                }

                FavouriteItem favouriteItem = new FavouriteItem(
                        SourceObjectHolder.getSources(this).getClass().getName()
                        , this.extraData.get("mangaUrl").toString()
                        , this.extraData.get("imageUrl").toString()
                        , this.extraData.get("mangaName").toString()
                        , (int) Instant.now().getEpochSecond()
                        , refererString);
                Favourites.checkWhatNeedsToHappen(this, favouriteItem);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }





}