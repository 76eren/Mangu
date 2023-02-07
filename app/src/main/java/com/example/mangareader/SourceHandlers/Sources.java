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

    class SearchValues {
        public String image;
        public String url;
        public String name;
        public String referer = null; // Not every class needs this so we can just default this to null
    }

    ArrayList<SearchValues> CollectDataPicScreen(String manga);

    String getStory(String url);

    class ValuesForChapters {
        // Some of these data aren't necessary for each source
        // In that case we can just leave theme empty
        public String url = "";
        public String name = "";
        public HashMap<String, Object> extraData = new HashMap<>();
    }

    ArrayList<ValuesForChapters> GetChapters(String url, Context context, HashMap<String, Object> extraData) throws IOException, NoSuchAlgorithmException, InvalidKeyException, JSONException;

    ArrayList<String> GetImages(ValuesForChapters object, Context context);

    HashMap<String, String> GetRequestData(String url);

    void PrepareReadChapter(ReadActivity readActivity);

    HashMap<String, ArrayList<HomeMangaClass>> GetDataHomeActivity(Context context) throws InterruptedException;

    String GetSourceName();
}