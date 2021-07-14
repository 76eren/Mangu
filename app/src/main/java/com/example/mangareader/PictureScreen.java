package com.example.mangareader;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.example.mangareader.Activities.ChaptersActivity;


import java.util.ArrayList;

public class PictureScreen {
    private int num = 0;

    private ImageView previous;
    private ImageView current;
    private ImageView next;
    private TextView name;
    private String command;
    private ArrayList<String> images;
    private ArrayList<String> names;
    private ArrayList<String> urls;
    private Context context;

    public void menu() {
        /*
        // This function handles the image showing part of the app
        // Possible bugs:
        // - I don't know what happens when the array size is < 3
        */
        Activity activity = (Activity) context;

        if (activity==null) {
            Log.d("lol", "act is null");
        }

        activity.runOnUiThread(() -> {

            previous.setVisibility(View.INVISIBLE);
            current.setVisibility(View.INVISIBLE);
            next.setVisibility(View.INVISIBLE);
            // We do our stuff with the menu in here
            int len = images.size();

            // Current
            current.setVisibility(View.VISIBLE);
            Glide.with(context).load(images.get(num)).into(current);
            name.setText(names.get(num));

            // previous
            if (num != 0) {
                previous.setVisibility(View.VISIBLE);
                Glide.with(context).load(images.get(num-1)).into(previous);
            }

            // next
            if (num+1 != len) {
                next.setVisibility(View.VISIBLE);
                Glide.with(context).load(images.get(num+1)).into(next);
            }
        });
    }

    // The reason why I decided to do it through is because it's less lines of code :)
    public void setup(ImageView prev, ImageView cur, ImageView nxt, TextView nme, String comm, ArrayList<String> imgs, ArrayList<String> nms, ArrayList<String> uls) {
        previous=prev;
        current=cur;
        next=nxt;
        name=nme;
        command=comm;
        images=imgs;
        names=nms;
        urls=uls;
        num=0;

        previous.setOnClickListener(v -> {
            if (num-1 >= 0) {
                num--;
                menu();
            }

        });
        next.setOnClickListener(v -> {
            if (num+1 <= images.size()) {
                num++;
                menu();
            }

        });

        current.setOnClickListener(v -> command());
    }

    public void command() {

        // Here we handle what happens next
        // The cmd command will tell us what function to call next
        // We do the stuff with the intents
        Intent intent = new Intent(context, ChaptersActivity.class);
        intent.putExtra("url", urls.get(num));
        intent.putExtra("img", images.get(num));

        switch (command) {
            case "mangakakalot_chapterList":
                intent.putExtra("source", "mangakakalot");
                break;
        }

        Activity activity = (Activity) this.context; // Idk wtf this is doing here.
        context.startActivity(intent);
    }


    public void AssignContext (Context ctx) {
        this.context=ctx;
    }
}
