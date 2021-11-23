package com.example.mangareader.Activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.mangareader.R;
import com.example.mangareader.Read;
import com.example.mangareader.ListTracker;
import com.example.mangareader.Settings;
import com.example.mangareader.ValueHolders.ObjectHolder;
import com.example.mangareader.SourceHandlers.Sources;
import com.example.mangareader.ValueHolders.ReadValueHolder;
import com.github.chrisbanes.photoview.PhotoView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class ReadActivity extends AppCompatActivity {

    public final Read read = new Read();
    public Sources source;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Ehhhh I want to die
        ListTracker.AddToList(this, ReadValueHolder.currentChapter.url, "History");

        setContentView(R.layout.activity_read);
        source = ObjectHolder.sources;

        Button prevClick = findViewById(R.id.prevPage);
        prevClick.setBackgroundColor(Color.TRANSPARENT);
        float x = 0;
        prevClick.setAlpha(x);
        Button nextClick = findViewById(R.id.nextPage);
        nextClick.setBackgroundColor(Color.TRANSPARENT);
        nextClick.setAlpha(x);


        final Boolean[] canClick = {true};
        prevClick.setOnClickListener(v -> {
            if (canClick[0]) {
                canClick[0] = false;
                read.Swipe(-1);
                canClick[0] = true;
            }
        });

        nextClick.setOnClickListener(v -> {
            if (canClick[0]) {
                canClick[0] = false;
                read.Swipe(1);
                canClick[0] = true;
            }
        });


        source.PrepareReadChapter(this); // Prepares our shit

        // MAKE THIS CALL ALREADY EXISTING FUNCTIONS IN THE READ CLASS
        // INSTEAD OF DOING IT ALL OVERT AGIAN
        new Thread(() -> {

            PhotoView photoView = findViewById(R.id.photo_view);
            TextView progress = findViewById(R.id.progress);


            Intent intent = getIntent();
            String chapterUrl = ReadValueHolder.currentChapter.url;

            // THIS DONT BELONG HERE IDIOT BAKA
            TextView cacheTV = findViewById(R.id.cache);
            cacheTV.setVisibility(View.INVISIBLE);



            ArrayList<String> imgs;
            imgs = this.source.GetImages(ReadValueHolder.currentChapter, this); // I am not really a big fan of calling ReadValueHolder rather than having a local variable. It's whatever though


            if (imgs == null) {
                // if this executeds, you have fucked up
                Log.d("lol", "An error occured whilst trying to load the list with chapters");
                Intent d = new Intent(this, MainActivity.class);
                startActivity(d);
                return;
            }


            imgs.removeAll(Collections.singleton(null));
            imgs.removeAll(Collections.singleton(""));

            HashMap<String,String> reqData = source.GetRequestData(chapterUrl);
            read.AssignData(this,imgs,photoView,progress,source, reqData); // We assign our context to read

            // Caching
            Settings settings = new Settings();
            Boolean shouldCache = settings.ReturnValueBoolean(this, "preference_Cache", false);

            if (shouldCache) {
                Log.d("lol", "caching");
                runOnUiThread(() -> cacheTV.setVisibility(View.VISIBLE));
                read.Cache();

                read.LoadGlide();
            }
            else  {
                Log.d("lol", "not caching");
                runOnUiThread(() -> cacheTV.setVisibility(View.INVISIBLE));
                // We start our shit
                read.LoadGlide();
            }



        }).start();

    }

}