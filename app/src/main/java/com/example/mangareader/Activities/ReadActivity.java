package com.example.mangareader.Activities;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.mangareader.R;
import com.example.mangareader.Read.Read;
import com.example.mangareader.Read.ReadClick;
import com.example.mangareader.ListTracker;
import com.example.mangareader.Read.ReadScroll;
import com.example.mangareader.Read.Readmodes;
import com.example.mangareader.Settings;
import com.example.mangareader.ValueHolders.DesignValueHolder;
import com.example.mangareader.ValueHolders.ObjectHolder;
import com.example.mangareader.SourceHandlers.Sources;
import com.example.mangareader.ValueHolders.ReadValueHolder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class ReadActivity extends AppCompatActivity {

    public Sources source;
    public Readmodes read;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        overridePendingTransition(0,0);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // Ehhhh I want to die
        ListTracker.AddToList(this, ReadValueHolder.currentChapter.url, "History");

        Settings settings = new Settings();
        if (!settings.ReturnValueBoolean(this, "preference_hardware_acceleration", false)) {
            getWindow().setFlags(
                    WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
                    WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
        }



        setContentView(R.layout.activity_read);
        source = ObjectHolder.sources;

        Button prevClick = findViewById(R.id.prevPage);
        prevClick.setBackgroundColor(Color.TRANSPARENT);
        float x = 0;
        prevClick.setAlpha(x);
        Button nextClick = findViewById(R.id.nextPage);
        nextClick.setBackgroundColor(Color.TRANSPARENT);
        nextClick.setAlpha(x);

        switch (settings.ReturnValueString(this, "read_mode", "click")) {
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


        source.PrepareReadChapter(this); // Prepares our shit

        // MAKE THIS CALL ALREADY EXISTING FUNCTIONS IN THE READ CLASS
        // INSTEAD OF DOING IT ALL OVERT AGIAN
        new Thread(() -> {

            TextView progress = findViewById(R.id.progress);

            // Allows hiding the progress bar
            progress.setOnClickListener(view -> {
                if (progress.getAlpha() == 0) {
                    progress.setAlpha(DesignValueHolder.ProgressBarAlphaWhenEnabled);
                }
                else {
                    progress.setAlpha(0);
                }

            });

            String chapterUrl = ReadValueHolder.currentChapter.url;

            // THIS DONT BELONG HERE IDIOT BAKA
            TextView cacheTV = findViewById(R.id.cache);
            cacheTV.setVisibility(View.INVISIBLE);


            ArrayList<String> imgs;
            imgs = this.source.GetImages(ReadValueHolder.currentChapter, this); // I am not really a big fan of calling ReadValueHolder rather than having a local variable. It's whatever though

            if (imgs == null) {
                Log.d("lol", "An error occured whilst trying to load the list with chapters");
                Intent d = new Intent(this, MainActivity.class);
                startActivity(d);
                return;
            }


            imgs.removeAll(Collections.singleton(null));
            imgs.removeAll(Collections.singleton(""));

            HashMap<String,String> reqData = source.GetRequestData(chapterUrl);
            read.Start(this, imgs, source, reqData); // We assign our context to read

            // Caching
            Boolean shouldCache = settings.ReturnValueBoolean(this, "preference_Cache", false);

            if (shouldCache) {
                Log.d("lol", "caching");
                runOnUiThread(() -> cacheTV.setVisibility(View.VISIBLE));
                Read.Cache(this, imgs, reqData);

                read.LoadImage();
            }
            else  {
                Log.d("lol", "not caching");
                runOnUiThread(() -> cacheTV.setVisibility(View.INVISIBLE));
                // We start our shit
                read.LoadImage();
            }



        }).start();

    }

}