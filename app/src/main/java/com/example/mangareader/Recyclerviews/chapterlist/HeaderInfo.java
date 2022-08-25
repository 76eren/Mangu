package com.example.mangareader.Recyclerviews.chapterlist;

public class HeaderInfo {

    private final String mangaName;
    private final String mangaUrl;
    private final String mangaImageUrl;
    private final String description;
    public HeaderInfo(String mangaName, String mangaUrl, String mangaImageUrl, String description) {
        this.mangaName = mangaName;
        this.mangaUrl = mangaUrl;
        this.mangaImageUrl = mangaImageUrl;
        this.description = description;
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

}
