package com.example.mangareader.Home;

public class HomeMangaClass {
    public String name;
    public String chapterUrl;
    public String image;
    public String referer;

    public HomeMangaClass(String name, String url, String image, String referer) {
        this.name = name;
        this.chapterUrl = url;
        this.image = image;
        this.referer = referer; // If there is no referer put null as the referer parameter
    }
}
