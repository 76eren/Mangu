package com.example.mangareader.Recyclerviews.chapterlist;

public class HeaderInfo {

    private final String mangaName;
    private final String mangaUrl;
    private final String mangaImageUrl;
    private final String description;
    private final String referer;

    public HeaderInfo(String mangaName, String mangaUrl, String mangaImageUrl, String description, String referer) {
        this.mangaName = mangaName;
        this.mangaUrl = mangaUrl;
        this.mangaImageUrl = mangaImageUrl;
        this.description = description;
        this.referer = referer;
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

    public String getReferer() {return referer;}

}
