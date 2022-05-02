package com.example.mangareader.Read;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.mangareader.ListTracker;
import com.example.mangareader.R;
import com.example.mangareader.Recyclerviews.RviewAdapterReadScroll;
import com.example.mangareader.Settings;
import com.example.mangareader.SourceHandlers.Sources;
import com.example.mangareader.ValueHolders.SourceObjectHolder;
import com.example.mangareader.ValueHolders.ReadValueHolder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;


public class ReadScroll implements Readmodes{
    private Data data;
    private boolean canSwitchTop = false;
    private boolean canSwitchBottom = false;

    @Override
    public void Start(Activity activity, ArrayList<String> images, Sources sources, HashMap<String, String> reqData) {
        this.data = new Data(activity, images, sources, reqData);

        // Disables the unnecessary widgets
        // Honestly I probably should've made a separate layout for each readmode but whatever this works too I guess
        this.data.activity.runOnUiThread(() -> {
            this.data.activity.findViewById(R.id.prevPage).setVisibility(View.GONE);
            this.data.activity.findViewById(R.id.nextPage).setVisibility(View.GONE);
            this.data.activity.findViewById(R.id.photo_view).setVisibility(View.GONE);

        });

    }

    @Override
    public void LoadImage() {

        TextView uwu = this.data.activity.findViewById(R.id.progress);
        this.data.activity.runOnUiThread(() -> uwu.setText(ReadValueHolder.getCurrentChapter(this.data.activity).name));

        List<RviewAdapterReadScroll.Data> data = new ArrayList<>();
        data.add(new RviewAdapterReadScroll.Data(this.data.activity, "", new HashMap<>(), "Previous chapter", this));
        for (String i : this.data.images) {
            data.add(new RviewAdapterReadScroll.Data(this.data.activity, i, this.data.reqData, "", this));
        }
        data.add(new RviewAdapterReadScroll.Data(this.data.activity, "", new HashMap<>(), "Next chapter", this));


        this.data.activity.runOnUiThread(() -> {

            RecyclerView recyclerView = this.data.activity.findViewById(R.id.recyclerview_read);
            RviewAdapterReadScroll adapter = new RviewAdapterReadScroll(this.data.activity, data, "imageview");
            recyclerView.setAdapter(adapter);

        });

    }

    @Override
    public void ChangePages(int direction) {
        // We don't need this
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


        if (direction == 1 &&  index+1 != ReadValueHolder.ChaptersActivityData.size()) {
            index++;
        }
        else if (direction == -1 && index-1 > -1) {
            index--;
        }

        Sources.ValuesForChapters newChapter = ReadValueHolder.ChaptersActivityData.get(index);
        ReadValueHolder.currentChapter = newChapter;

        new Thread(() -> {
            Sources sources = SourceObjectHolder.getSources(this.data.activity);
            ArrayList<String> images = sources.GetImages(newChapter, this.data.activity);
            images.removeAll(Collections.singleton(null));
            images.removeAll(Collections.singleton(""));
            // We want to edit a few values
            this.data.images = images;

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



