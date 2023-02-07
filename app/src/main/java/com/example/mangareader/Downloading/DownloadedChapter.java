package com.example.mangareader.Downloading;

public class DownloadedChapter {
    private String source;
    private String url;
    private String image;
    private String mangaName;
    private String mangaStory;
    private int date = 0;
    private String referer;
    private String chapterName;

    private String[] imageNames;
    private String imagesPath;


    public DownloadedChapter(String source, String url, String image, String mangaName, int date, String referer, String mangaStory, String chapterName, String[] imageNames,
                             String imagesPath) {
        this.source = source;
        this.url = url;
        this.image = image;
        this.mangaName = mangaName;
        this.chapterName = chapterName;
        this.date = date;
        this.referer = referer;
        this.mangaStory = mangaStory;
        this.imageNames = imageNames;
        this.imagesPath = imagesPath;

    }

    public String getChapterName() {
        return chapterName;
    }

    public void setChapterName(String chapterName) {
        this.chapterName = chapterName;
    }

    public int returnDate() {
        return this.date;
    }

    public String getName() {
        return this.mangaName;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getMangaName() {
        return mangaName;
    }

    public void setMangaName(String mangaName) {
        this.mangaName = mangaName;
    }

    public String getMangaStory() {
        return mangaStory;
    }

    public void setMangaStory(String mangaStory) {
        this.mangaStory = mangaStory;
    }

    public int getDate() {
        return date;
    }

    public void setDate(int date) {
        this.date = date;
    }

    public String getReferer() {
        return referer;
    }

    public void setReferer(String referer) {
        this.referer = referer;
    }

    public String[] getImageNames() {
        return imageNames;
    }

    public void setImageNames(String[] imageNames) {
        this.imageNames = imageNames;
    }

    public String getImagesPath() {
        return imagesPath;
    }

    public void setImagesPath(String imagesPath) {
        this.imagesPath = imagesPath;
    }
}