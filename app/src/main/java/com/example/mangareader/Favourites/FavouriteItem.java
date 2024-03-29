package com.example.mangareader.Favourites;

public class FavouriteItem {
    public String source;
    public String url;
    public String image;
    public String mangaName;
    public int date = 0;
    public String referer;

    public FavouriteItem(String source, String url, String image, String mangaName, int date, String referer) {
        this.source = source;
        this.url = url;
        this.image = image;
        this.mangaName = mangaName;
        this.date = date;
        this.referer = referer;
    }

    // These functions are needed for the sorting
    public int returnDate() {
        return this.date;
    }

    public String getName() {
        return this.mangaName;
    }

}
