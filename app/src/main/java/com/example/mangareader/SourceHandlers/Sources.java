package com.example.mangareader.SourceHandlers;

import android.content.Context;
import com.example.mangareader.Activities.ReadActivity;
import com.example.mangareader.Home.HomeMangaClass;

import java.util.ArrayList;
import java.util.HashMap;

public interface Sources {

     class SearchValues {
          public String image;
          public String url;
          public String name;
     }

     ArrayList<SearchValues> CollectDataPicScreen(String manga);

     String getStory(String url);

     class ValuesForChapters {
          // Some of these data aren't neccesary for each source
          // In that case we can just leave theme empty

          public String url = "";
          public String name = "";
          public HashMap<String,String> extraData = new HashMap<>();

     }

     ArrayList<ValuesForChapters> GetChapters(String url, Context context);

     ArrayList<String> GetImages(ValuesForChapters object, Context context);

     HashMap<String,String> GetRequestData(String url);

     void PrepareReadChapter(ReadActivity readActivity);



     HashMap<String, ArrayList<HomeMangaClass>> GetDataHomeActivity();

}
