package com.example.mangareader.Read;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.example.mangareader.ListTracker;
import com.example.mangareader.R;
import com.example.mangareader.Settings;
import com.example.mangareader.SourceHandlers.Sources;
import com.example.mangareader.ValueHolders.SourceObjectHolder;
import com.example.mangareader.ValueHolders.ReadValueHolder;
import com.github.chrisbanes.photoview.PhotoView;

import java.util.*;



public class ReadClick implements Readmodes{
    private Data data;
    private PhotoView photoView;
    int page = 0;

    private TextView progress;

    @Override
    public void Start(Activity activity, ArrayList<String> images, Sources sources, HashMap<String, String> reqData) {
        this.data = new Data(activity, images, sources, reqData);
        this.photoView = activity.findViewById(R.id.photo_view);

        Button next = activity.findViewById(R.id.nextPage);
        Button previous = activity.findViewById(R.id.prevPage);
        this.progress = activity.findViewById(R.id.progress);

        // Controls the page switching
        next.setOnClickListener(view -> ChangePages(1));
        previous.setOnClickListener(view -> ChangePages(-1));

        // Disables widgets we don't need
        this.data.activity.runOnUiThread(() ->this.data.activity.findViewById(R.id.recyclerview_read).setVisibility(View.GONE));
    }

    @Override
    public void LoadImage() {
        Log.d("lol", data.images.get(page).trim());
        this.data.activity.runOnUiThread(() -> {
            Read.LoadImage(data.images.get(page), photoView, this.data.reqData, this.data.activity);
        });

        this.progress.setText(this.page+1 + "/"+ this.data.images.size() + " - "+ ReadValueHolder.getCurrentChapter(this.data.activity).name);
    }

    @Override
    public void ChangePages(int direction) {
        page += direction;

        // We go to the next chapter
        if (page >= this.data.images.size()) {
            ChangeChapter(1); // 1 = next, -1 = previous
            return;
        }

        // We go to the previous page
        if (page < 0) {
            ChangeChapter(-1); // 1 = next, -1 = previous
            return;
        }


        LoadImage();
    }

    @Override
    public void ChangeChapter(int direction) {
        String temp = ReadValueHolder.currentChapter.url;

        int index = 0;
        for (Sources.ValuesForChapters i : ReadValueHolder.ChaptersActivityData) {
            if (temp.equals(i.url)) {
                index = ReadValueHolder.ChaptersActivityData.indexOf(i);
                break;
            }
        }
        if (direction == 1 && index+1 != ReadValueHolder.ChaptersActivityData.size()) {
            index++;
        }
        else if (direction == -1 && index-1 > -1) {
            index--;
        }

        Sources.ValuesForChapters newChapter = ReadValueHolder.ChaptersActivityData.get(index);
        ReadValueHolder.currentChapter = newChapter;

        new Thread(() -> {

            ArrayList<String> images = SourceObjectHolder.getSources(this.data.activity).GetImages(newChapter, this.data.activity);
            images.removeAll(Collections.singleton(null));
            images.removeAll(Collections.singleton(""));
            // We want to edit a few values
            this.data.images = images;
            this.page=0;

            // Adds our chapter to the history
            ListTracker.AddToList(this.data.activity, ReadValueHolder.currentChapter.url, "History");

            Settings settings = new Settings();
            if (settings.ReturnValueBoolean(this.data.activity, "preference_Cache", false)) {
                Read.Cache(this.data.activity, this.data.images, this.data.reqData);
            }

            LoadImage();


        }).start();

    }


}
