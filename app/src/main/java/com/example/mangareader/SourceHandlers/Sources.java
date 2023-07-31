package com.example.mangareader.SourceHandlers;

import android.content.Context;
import com.example.mangareader.Activities.ReadActivity;
import com.example.mangareader.Home.HomeMangaClass;
import org.json.JSONException;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;

public interface Sources {
    // Here is a list of things you need to do when adding a new source
    // - Create a new class and make it implement this interface
    // - Go to the SettingsActivity/root_preferences and add the new source to it.
    // - Go to SourceObjectHolder, inside the getSources function and add a new element to the switch statement
    // - Make sure the new source has a GetSourceName function
    // - Go to the SettingsActivity, inside the onCreatePreferences function add a new add a new value to the switch statement inside the sources section
    // - - Go to the SettingsActivity, inside the onCreatePreferences function add a new setOnPreferenceClickListener that reflects the new source

    ArrayList<SearchValues> collectDataPicScreen(String manga);

    String getStory(String url);

    ArrayList<ValuesForChapters> getChapters(String url, Context context, HashMap<String, Object> extraData) throws IOException, NoSuchAlgorithmException, InvalidKeyException, JSONException;

    ArrayList<String> getImages(ValuesForChapters object, Context context);

    HashMap<String, String> getRequestData(String url);

    void prepareReadChapter(ReadActivity readActivity);

    HashMap<String, ArrayList<HomeMangaClass>> getDataHomeActivity(Context context) throws InterruptedException;

    String getSourceName();

    class SearchValues {
        public String image;
        public String url;
        public String name;
        public String referer = null; // Not every class needs this so we can just default this to null
    }

    class ValuesForChapters {
        // Some of these data aren't necessary for each source
        // In that case we can just leave theme empty
        public String url = "";
        public String name = "";
        public HashMap<String, Object> extraData = new HashMap<>();
    }
}