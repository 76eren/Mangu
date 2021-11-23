package com.example.mangareader.SourceHandlers;

import android.content.Context;
import com.example.mangareader.Activities.ReadActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

public interface Sources {

     class ValuesForCollectDataPicScreen {

          public String image;
          public String url;
          public String name;
     }

     ArrayList<ValuesForCollectDataPicScreen> CollectDataPicScreen(String manga);

     String getStory(String url);

     class ValuesForChapters {
          public String url;
          public String name;
          public String volume = "";
          public HashMap<String,String> extraData = new HashMap<>();

     }

     ArrayList<ValuesForChapters> GetChapters(String url);

     ArrayList<String> GetImages(ValuesForChapters object, Context context);

     HashMap<String,String> GetRequestData(String url);

     void PrepareReadChapter(ReadActivity readActivity);


}
