package com.example.mangareader.Read;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.example.mangareader.Downloading.DownloadTracker;
import com.example.mangareader.Downloading.DownloadedChapter;
import com.example.mangareader.Settings.ListTracker;
import com.example.mangareader.R;
import com.example.mangareader.Settings.Settings;
import com.example.mangareader.SourceHandlers.Sources;
import com.example.mangareader.ValueHolders.ReadValueHolder;
import com.example.mangareader.ValueHolders.SourceObjectHolder;
import com.github.chrisbanes.photoview.PhotoView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;

public class ReadClick implements Readmodes {
    int page = 0;
    private Data data;
    private Readmodes.DownloadData dataDownload;
    private PhotoView photoView;
    private TextView progress;


    @SuppressLint("SetTextI18n")
    @Override
    public void loadImageDownload() {
        this.dataDownload.activity.runOnUiThread(() -> {

            Read.loadImageDownload(dataDownload.activity, this.photoView, this.dataDownload.chapterDatas.get(0).getImageNames()[page], this.dataDownload.chapterDatas.get(0).getImagesPath());

            this.progress.setText(this.page + 1 + "/" + this.dataDownload.chapterDatas.get(0).getImageNames().length + " - "
                    + ReadValueHolder.getCurrentChapter(this.data.activity).name);

        });
    }

    @Override
    public void inflate(Activity activity) {
        LayoutInflater inflater = (LayoutInflater) activity.getApplicationContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View vieww = inflater.inflate(R.layout.readclick, null);
        ConstraintLayout constraintLayout = activity.findViewById(R.id.layout_readactivity);
        constraintLayout.addView(vieww);
    }

    @Override
    public void start(Activity activity, ArrayList<String> images, Sources sources, HashMap<String, String> reqData) {
        this.data = new Data(activity, images, sources, reqData);

        this.photoView = activity.findViewById(R.id.photo_view);

        Button next = activity.findViewById(R.id.nextPage);
        Button previous = activity.findViewById(R.id.prevPage);
        this.progress = activity.findViewById(R.id.progress);

        // Controls the page switching
        next.setOnClickListener(view -> changePages(1));
        previous.setOnClickListener(view -> changePages(-1));

        activity.runOnUiThread(() -> {
            previous.setBackgroundColor(Color.TRANSPARENT);
            previous.setAlpha(0);
            next.setBackgroundColor(Color.TRANSPARENT);
            next.setAlpha(0);
        });

    }


    @Override
    public void startDownloads(Activity activity, ArrayList<DownloadedChapter> downloads, Sources sources, HashMap<String, String> reqData) {
        this.dataDownload = new DownloadData(activity, downloads, sources, reqData);
        this.photoView = activity.findViewById(R.id.photo_view);


        Button next = activity.findViewById(R.id.nextPage);
        Button previous = activity.findViewById(R.id.prevPage);
        this.progress = activity.findViewById(R.id.progress);

        // Controls the page switching
        next.setOnClickListener(view -> changePagesDownload(1));
        previous.setOnClickListener(view -> changePagesDownload(-1));

        activity.runOnUiThread(() -> {
            previous.setBackgroundColor(Color.TRANSPARENT);
            previous.setAlpha(0);
            next.setBackgroundColor(Color.TRANSPARENT);
            next.setAlpha(0);
        });
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void loadImage() {
        this.data.activity.runOnUiThread(() -> {
            Read.loadImage(data.images.get(page), photoView, this.data.reqData, this.data.activity);
            this.progress.setText(this.page + 1 + "/" + this.data.images.size() + " - "
                    + ReadValueHolder.getCurrentChapter(this.data.activity).name);
        });

    }

    @Override
    public void changePages(int direction) {
        page += direction;

        // We go to the next chapter
        if (page >= this.data.images.size()) {
            changeChapter(1); // 1 = next, -1 = previous
            return;
        }

        // We go to the previous page
        if (page < 0) {
            changeChapter(-1); // 1 = next, -1 = previous
            return;
        }

        loadImage();
    }

    @Override
    public void changeChapter(int direction) {
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

        } else if (direction == -1 && index - 1 >= 0) {
            index--;

        }

        Sources.ValuesForChapters newChapter = ReadValueHolder.ChaptersActivityData.get(index);
        ReadValueHolder.currentChapter = newChapter;

        new Thread(() -> {

            ArrayList<String> images = SourceObjectHolder.getSources(this.data.activity).getImages(newChapter,
                    this.data.activity);
            images.removeAll(Collections.singleton(null));
            images.removeAll(Collections.singleton(""));
            // We want to edit a few values
            this.data.images = images;
            this.page = 0;

            // Adds our chapter to the history
            ListTracker.addToList(this.data.activity, ReadValueHolder.currentChapter.url, "History");

            Settings settings = new Settings();
            if (settings.returnValueBoolean(this.data.activity, "preference_Cache", false)) {
                Read.cache(this.data.activity, this.data.images, this.data.reqData);
            }

            loadImage();

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
        } else if (direction == -1 && index - 1 > -1) {
            index--;
        }


        ReadValueHolder.currentChapter = ReadValueHolder.ChaptersActivityData.get(index);

        new Thread(() -> {
            ListTracker.addToList(this.dataDownload.activity, ReadValueHolder.currentChapter.url, "History");

            DownloadTracker downloadTracker = new DownloadTracker();
            ArrayList<DownloadedChapter> finalDownloads = new ArrayList<>();
            LinkedHashSet<DownloadedChapter> tempDownloads = downloadTracker.getFromDownloads(dataDownload.activity);
            for (DownloadedChapter i : tempDownloads) {
                if (i.getUrl().equals(ReadValueHolder.getCurrentChapter(this.dataDownload.activity).url)) {
                    finalDownloads.add(i);
                }
            }

            this.page = 0;
            startDownloads(dataDownload.activity, finalDownloads, dataDownload.sources, dataDownload.reqData);
            loadImageDownload();

        }).start();
    }


    @Override
    public void changePagesDownload(int direction) {
        page += direction;

        // We go to the next chapter
        if (page >= this.dataDownload.chapterDatas.get(0).getImageNames().length) {
            changeChapterDownloads(1); // 1 = next, -1 = previous
            return;
        }

        // We go to the previous page
        if (page < 0) {
            changeChapterDownloads(-1); // 1 = next, -1 = previous
            return;
        }

        loadImageDownload();
    }

}
