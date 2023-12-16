package com.example.mangareader.Recyclerviews.chapterlist;

import java.util.HashMap;

public class HeaderInfo {

    private final String mangaName;
    private final String mangaUrl;
    private final String mangaImageUrl;
    private final String description;
    private final String referer;
    private HashMap<String, Object> extraData = null;
    private boolean isDownloaded;


    public HeaderInfo(String mangaName, String mangaUrl, String mangaImageUrl, String description, String referer, HashMap<String, Object> extraData, boolean isDownloaded) {
        this.mangaName = mangaName;
        this.mangaUrl = mangaUrl;
        this.mangaImageUrl = mangaImageUrl;
        this.description = description;
        this.referer = referer;
        this.extraData = extraData;
        this.isDownloaded = isDownloaded;
    }

    public String getMangaName() {
        return mangaName;
    }

    public String getMangaUrl() {
        return mangaUrl;
    }

    public String getMangaImageUrl() {
        return mangaImageUrl;
    }

    public String getDescription() {
        return description;
    }

    public String getReferer() {
        return referer;
    }

    public HashMap<String, Object> getExtraData() {
        return extraData;
    }
}
