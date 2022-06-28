package com.example.mangareader.Favourites;

public class FavouriteItem {
    public String source;
    public String url = "";
    public String image = "";
    public String mangaName = "[NO NAME]";
    public int date = 0;

    public FavouriteItem(String source, String url, String image, String mangaName, int date) {
        this.source = source;
        this.url = url;
        this.image = image;
        this.mangaName = mangaName;
        this.date = date;
    }

    // These functions are needed for the sorting
    public int returnDate() {
        return this.date;
    }

    public String returnName() {
        return this.mangaName;
    }

}
