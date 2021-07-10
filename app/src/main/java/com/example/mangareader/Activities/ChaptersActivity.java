package com.example.mangareader.Activities;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.bumptech.glide.Glide;
import com.example.mangareader.SourceHandlers.ChapterListSourceHandlers;
import com.example.mangareader.SplashScreen;
import com.example.mangareader.R; // Not importing this will break the whole thingy lol
import com.example.mangareader.Read;
import com.example.mangareader.mangakakalot.Mangakakalot;

import java.util.LinkedHashMap;

public class ChaptersActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chapters);
        // Define our widgets
        ImageView cover = findViewById(R.id.cover);
        TextView story = findViewById(R.id.story);
        TextView status = findViewById(R.id.status);
        LinearLayout layout = findViewById(R.id.llayout);
        layout.setOrientation(LinearLayout.VERTICAL);

        cover.setVisibility(View.INVISIBLE);
        story.setVisibility(View.INVISIBLE);
        layout.setVisibility(View.INVISIBLE);

        // Sets our status
        status.setText(SplashScreen.returnQuote());

        // Changes our story layout
        LinearLayout.LayoutParams storyParamsWrap = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        storyParamsWrap.setMargins(5,10,5,10);
        story.setOnClickListener(v -> {
            story.setLayoutParams(storyParamsWrap);
        });

        // First we retrieve the url
        Intent intent = getIntent();
        String chapterUrl = intent.getStringExtra("url");
        String imageUrl = intent.getStringExtra("img");

        // We put this on a different thread to speed things up
        new Thread(() -> runOnUiThread(() -> {
            Glide.with(this).load(imageUrl).into(cover);
        })).start();


        new Thread(() -> {

            String mangaStory = null;
            LinkedHashMap<String,String> Stalin = null;

            ChapterListSourceHandlers chapterListSourceHandlers = new ChapterListSourceHandlers();
            chapterListSourceHandlers.GetSource(intent.getStringExtra("source"), chapterUrl);


            mangaStory = chapterListSourceHandlers.mangaStory;
            Stalin = chapterListSourceHandlers.Stalin;



            Mangakakalot mangakakalot = new Mangakakalot();
            mangaStory = mangakakalot.getStory(chapterUrl); // We might want to trim this?????
            // We now start scraping all of the chapters
            Stalin = mangakakalot.GetChapters(chapterUrl);


            Read.chaptersXurls = Stalin; // So we assign it statically which I am not a big fan of tbh.
            // Now we have to generate all of the buttons
            for (String key : Stalin.keySet()) {

                // We create a new button
                Button button = new Button(this);
                button.setText(key);

                // give the button a nice style
                // totally didn't steal from here https://stackoverflow.com/questions/34075131/how-to-set-a-button-border-color-programmatically-in-android/34075906
                ShapeDrawable shapedrawable = new ShapeDrawable();
                shapedrawable.setShape(new RectShape());
                shapedrawable.getPaint().setColor(Color.BLACK);
                shapedrawable.getPaint().setStrokeWidth(10f);
                shapedrawable.getPaint().setStyle(Paint.Style.STROKE);
                button.setBackground(shapedrawable);


                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT
                );
                params.setMargins(5,10,5,10);
                button.setLayoutParams(params);

                LinkedHashMap<String, String> finalStalin = Stalin;
                button.setOnClickListener(v -> {
                    // Now we have to save our choice and go to the next chapter
                    String url = finalStalin.get(key); // our url
                    Intent readIntent = new Intent(this, ReadActivity.class);
                    readIntent.putExtra("url", url);
                    readIntent.putExtra("chapter", key);

                    startActivity(readIntent);

                    return;
                });

                runOnUiThread(() -> {
                    layout.addView(button);
                });
            }

            String finalMangaStory = mangaStory;
            runOnUiThread(() -> {
                story.setText(finalMangaStory);
                // We want the cover+story to appear at the exact same time
                story.setVisibility(View.VISIBLE);
                cover.setVisibility(View.VISIBLE);
                status.setVisibility(View.GONE);
                layout.setVisibility(View.VISIBLE);
            });

        }).start();
    }

}