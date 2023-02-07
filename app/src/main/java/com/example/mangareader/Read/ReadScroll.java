package com.example.mangareader.Read;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import com.example.mangareader.Downloading.DownloadTracker;
import com.example.mangareader.Downloading.DownloadedChapter;
import com.example.mangareader.ListTracker;
import com.example.mangareader.R;
import com.example.mangareader.Recyclerviews.RviewAdapterReadScroll;
import com.example.mangareader.Settings;
import com.example.mangareader.SourceHandlers.Sources;
import com.example.mangareader.ValueHolders.SourceObjectHolder;
import com.example.mangareader.ValueHolders.ReadValueHolder;

import java.util.*;

public class ReadScroll implements Readmodes {
    private Data data = null;
    public DownloadData dataDownload = null;



    @Override
    public void inflate(Activity activity) {
        activity.runOnUiThread(() -> {

            LayoutInflater inflater = (LayoutInflater) activity.getApplicationContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.readscroll, null);
            ConstraintLayout constraintLayout = activity.findViewById(R.id.layout_readactivity);
            constraintLayout.addView(view);

        });
    }

    @Override
    public void Start(Activity activity, ArrayList<String> images, Sources sources, HashMap<String, String> reqData) {
        this.data = new Data(activity, images, sources, reqData);
    }

    @Override
    public void startDownloads(Activity activity, ArrayList<DownloadedChapter> downloads, Sources sources, HashMap<String, String> reqData) {
        this.dataDownload = new DownloadData(activity, downloads, sources, reqData);

    }

    @Override
    public void loadImageDownload() {
        TextView uwu = this.data.activity.findViewById(R.id.progress);
        this.data.activity.runOnUiThread(() -> uwu.setText(ReadValueHolder.getCurrentChapter(this.data.activity).name));
        List<RviewAdapterReadScroll.DataDownload> data = new ArrayList<>();
        data.add(new RviewAdapterReadScroll.DataDownload(this.dataDownload.activity, "", "", "Previous chapter", this, this.dataDownload));
        int index = 0;

        for (String i : this.dataDownload.chapterDatas.get(0).getImageNames()) {
            data.add(new RviewAdapterReadScroll.DataDownload(this.dataDownload.activity, i, this.dataDownload.chapterDatas.get(0).getImagesPath(), "", this, this.dataDownload));
            index++;

        }

        data.add(new RviewAdapterReadScroll.DataDownload(this.dataDownload.activity, "", "", "Next chapter", this, this.dataDownload));
        Activity activity = this.dataDownload.activity;
        this.dataDownload.activity.runOnUiThread(() -> {
            RecyclerView recyclerView = activity.findViewById(R.id.recyclerview_read);
            recyclerView.setHasFixedSize(true);

            RviewAdapterReadScroll adapter = new RviewAdapterReadScroll(activity, null, data);
            recyclerView.setAdapter(adapter);
        });


    }


    @Override
    public void LoadImage() {
        TextView uwu = this.data.activity.findViewById(R.id.progress);
        this.data.activity.runOnUiThread(() -> uwu.setText(ReadValueHolder.getCurrentChapter(this.data.activity).name));

        List<RviewAdapterReadScroll.Data> data = new ArrayList<>();
        data.add(new RviewAdapterReadScroll.Data(this.data.activity, "", new HashMap<>(), "Previous chapter", this, this.dataDownload));
        for (String i : this.data.images) {
            data.add(new RviewAdapterReadScroll.Data(this.data.activity, i, this.data.reqData, "", this, this.dataDownload));
        }

        data.add(new RviewAdapterReadScroll.Data(this.data.activity, "", new HashMap<>(), "Next chapter", this, this.dataDownload));

        this.data.activity.runOnUiThread(() -> {

            RecyclerView recyclerView = this.data.activity.findViewById(R.id.recyclerview_read);
            recyclerView.setHasFixedSize(true);

            RviewAdapterReadScroll adapter = new RviewAdapterReadScroll(this.data.activity, data, null);
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

        if (direction == 1 && index + 1 != ReadValueHolder.ChaptersActivityData.size()) {
            index++;
        } else if (direction == -1 && index - 1 > -1) {
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

    @Override
    public void changeChapterDownloads(int direction) {
        String temp = ReadValueHolder.currentChapter.url;

        int index = 0;
        for (Sources.ValuesForChapters i : ReadValueHolder.ChaptersActivityData) {
            if (temp.equals(i.url)) {
                index = ReadValueHolder.ChaptersActivityData.indexOf(i);
                break;
            }
        }

        if (direction == 1 && index + 1 != ReadValueHolder.ChaptersActivityData.size()) {
            index++;
        }
        else if (direction == -1 && index - 1 > -1) {
            index--;
        }


        ReadValueHolder.currentChapter = ReadValueHolder.ChaptersActivityData.get(index);

        new Thread(() -> {
            ListTracker.AddToList(this.data.activity, ReadValueHolder.currentChapter.url, "History");

            DownloadTracker downloadTracker = new DownloadTracker();
            ArrayList<DownloadedChapter> finalDownloads = new ArrayList<>();
            LinkedHashSet<DownloadedChapter> tempDownloads = downloadTracker.getFromDownloads(dataDownload.activity);
            for (DownloadedChapter i : tempDownloads) {
                if (i.getUrl().equals(ReadValueHolder.getCurrentChapter(this.dataDownload.activity).url)) {
                    finalDownloads.add(i);
                }
            }

            startDownloads(dataDownload.activity, finalDownloads, dataDownload.sources, dataDownload.reqData);
            loadImageDownload();

        }).start();
    }

    @Override
    public void ChangePagesDownload(int direction) {

    }
}
