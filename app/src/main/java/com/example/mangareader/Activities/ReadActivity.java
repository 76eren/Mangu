package com.example.mangareader.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.mangareader.Downloading.DownloadedChapter;
import com.example.mangareader.Settings.ListTracker;
import com.example.mangareader.R;
import com.example.mangareader.Read.Read;
import com.example.mangareader.Read.ReadClick;
import com.example.mangareader.Read.ReadScroll;
import com.example.mangareader.Read.Readmodes;
import com.example.mangareader.Settings.Settings;
import com.example.mangareader.SourceHandlers.Sources;
import com.example.mangareader.ValueHolders.DesignValueHolder;
import com.example.mangareader.ValueHolders.ReadValueHolder;
import com.example.mangareader.ValueHolders.SourceObjectHolder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;

public class ReadActivity extends AppCompatActivity {

    public Sources source;
    public Readmodes read;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        overridePendingTransition(0, 0);

        ListTracker.addToList(this, ReadValueHolder.getCurrentChapter(this).url, "History");
        Settings settings = new Settings();
        Intent readIntent = getIntent();
        boolean isDownloaded = readIntent.getBooleanExtra("downloaded", false);

        if (!settings.returnValueBoolean(this, "preference_hardware_acceleration", false)) {
            getWindow().setFlags(
                    // I have hardware acceleration turned off by default for this activity
                    // This'll enable it when the setting allows us to
                    WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
                    WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
        }

        setContentView(R.layout.activity_read);

        source = SourceObjectHolder.getSources(this);

        // The default value MUST reflect the default value of the root proferences!!!!
        switch (settings.returnValueString(this, "read_mode", "scroll")) {
            case "click":
                read = new ReadClick();
                break;

            case "scroll":
                read = new ReadScroll();
                break;

            default:
                new ReadClick();
                break;
        }


        read.inflate(this);

        source.prepareReadChapter(this); // Prepares the readchapter

        new Thread(() -> {
            TextView progress = findViewById(R.id.progress);

            // Allows hiding the progress bar
            progress.setOnClickListener(view -> {
                if (progress.getAlpha() == 0) {
                    progress.setAlpha(DesignValueHolder.progressBarAlphaWhenEnabled);
                } else {
                    progress.setAlpha(0);
                }
            });

            String chapterUrl = ReadValueHolder.getCurrentChapter(this).url;

            // THIS DOESN'T BELONG HERE
            TextView cacheTV = findViewById(R.id.cache);
            cacheTV.setVisibility(View.INVISIBLE);

            ArrayList<String> imgs = new ArrayList<>();
            ArrayList<DownloadedChapter> finalDownloads = new ArrayList<>();

            // I am not really a big fan of calling ReadValueHolder rather than having a local variable.
            // It's whatever though
            imgs = SourceObjectHolder.getSources(this).getImages(ReadValueHolder.getCurrentChapter(this), this);

            // This usually runs after inactivity.....
            if (imgs == null) {
                Intent intent = new Intent(this, HomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                return;
            }

            imgs.removeAll(Collections.singleton(null));
            imgs.removeAll(Collections.singleton(""));

            HashMap<String, String> reqData = source.getRequestData(chapterUrl);

            read.start(this, imgs, source, reqData); // We assign our context to read

            // Caching
            Boolean shouldCache = settings.returnValueBoolean(this, "preference_Cache", false);
            if (shouldCache) {
                runOnUiThread(() -> cacheTV.setVisibility(View.VISIBLE));
                Read.cache(this, imgs, reqData);
                read.loadImage();
            }
            else {
                runOnUiThread(() -> cacheTV.setVisibility(View.INVISIBLE));
                read.loadImage();
            }

        }).start();

    }

}