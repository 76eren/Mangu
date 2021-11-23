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
import com.example.mangareader.SourceHandlers.Sources;


import java.util.ArrayList;

public class PictureScreen {
    private int num = 0;

    private ImageView previous;
    private ImageView current;
    private ImageView next;
    private TextView name;

    private  ArrayList<Sources.ValuesForCollectDataPicScreen> data = new ArrayList<>();

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
            int len = this.data.size();

            // Current
            current.setVisibility(View.VISIBLE);
            Glide.with(context).load(data.get(num).image).into(current);
            name.setText(data.get(num).name);

            // previous
            if (num != 0) {
                previous.setVisibility(View.VISIBLE);
                Glide.with(context).load(this.data.get(num-1).image).into(previous);
            }

            // next
            if (num+1 != len) {
                next.setVisibility(View.VISIBLE);
                Glide.with(context).load(this.data.get(num+1).image).into(next);
            }
        });
    }

    // The reason why I decided to do it through is because it's less lines of code :)
    public void setup(ImageView previous, ImageView current, ImageView next, TextView name, ArrayList<Sources.ValuesForCollectDataPicScreen> data) {
        this.previous=previous;
        this.current=current;
        this.next=next;
        this.name=name;
        this.data = data;

        this.num=0;

        Activity activity = (Activity) context;
        activity.runOnUiThread(() -> {
            previous.setOnClickListener(v -> {
                if (num-1 >= 0) {
                    num--;
                    menu();
                }

            });
            next.setOnClickListener(v -> {
                if (num+1 <= this.data.size()) {
                    num++;
                    menu();
                }

            });

            current.setOnClickListener(v -> command());


        });


    }

    // Don't forget to cahnge the fun name to something that makeds sense
    public void command() {

        Intent intent = new Intent(context, ChaptersActivity.class);

        intent.putExtra("url", data.get(num).url);
        intent.putExtra("img", data.get(num).image);

        context.startActivity(intent);
    }


    public void AssignContext (Context ctx) {
        this.context=ctx;
    }
}
