package com.example.mangareader.SourceHandlers;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public interface Sources {
     ArrayList<String> images = new ArrayList<>();
     ArrayList<String> urls = new ArrayList<>();
     ArrayList<String> names = new ArrayList<>();
     void GetLinks(String manga);

     String getStory(String url);

     LinkedHashMap<String,String> GetChapters(String url);


     ArrayList<String> GetImages(String url);

}
