package com.example.mangareader.Recyclerviews;

import android.content.Context;
import com.example.mangareader.SourceHandlers.Sources;

public class RviewChapterlistDataClass {
    public String name;
    public String tv;
    public String image;
    public Sources.ValuesForChapters btn;
    public String extraData;
    public Context context;

    public RviewChapterlistDataClass(String name, String tv, String image, Sources.ValuesForChapters btn, String extraData, Context context) {
        this.name = name;
        this.tv = tv;
        this.image = image;
        this.btn = btn;
        this.extraData = extraData;
        this.context = context;
    }
}
