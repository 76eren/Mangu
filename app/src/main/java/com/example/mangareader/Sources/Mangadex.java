package com.example.mangareader.Sources;

import android.app.DownloadManager;
import android.content.Context;
import android.util.Log;
import com.example.mangareader.Activities.ReadActivity;
import com.example.mangareader.SourceHandlers.Sources;
import com.example.mangareader.ValueHolders.ReadValueHolder;
import com.google.gson.JsonArray;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.internal.StringUtil;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class Mangadex implements Sources {
    @Override
    public ArrayList<ValuesForCollectDataPicScreen> CollectDataPicScreen(String manga) {
        // We first need to call the API
        OkHttpClient client = new OkHttpClient();
        try {
            String URL = "https://api.mangadex.org/manga?title="+manga+"&includes[]=cover_art&order[relevance]=desc";

            Request request = new Request.Builder()
                    .url(URL)
                    .build();

            String body;
            try (Response response = client.newCall(request).execute()) {
                body =  response.body().string();
            }

            if (!body.equals("")) {
                // Now we can start getting the fucking data!!

                // We'll store the objects with data inside this arraylist and return it later on
                ArrayList<ValuesForCollectDataPicScreen> values = new ArrayList<>();

                JSONObject jsonObject = new JSONObject(body);
                JSONArray jsonArray = jsonObject.getJSONArray("data");
                for (int i = 0; i < jsonArray.length(); i++) {

                    try {
                        JSONObject ob = jsonArray.getJSONObject(i);
                        String id = ob.getString("id");
                        String url = "https://mangadex.org/title/"+id; // ADD THIS

                        JSONObject x = ob.getJSONObject("attributes");
                        JSONObject y = x.getJSONObject("title");
                        String title = y.getString("en"); // ADD THIS

                        // Image link
                        // https://uploads.mangadex.org/covers/ID/FILENAME
                        JSONArray relationships = ob.getJSONArray("relationships");
                        JSONObject two = relationships.getJSONObject(2);
                        JSONObject attributes = two.getJSONObject("attributes");
                        String fileName = attributes.getString("fileName");
                        String image = "https://uploads.mangadex.org/covers/"+id+"/"+fileName;

                        ValuesForCollectDataPicScreen obbie = new ValuesForCollectDataPicScreen();
                        obbie.name = title;
                        obbie.url = url;
                        obbie.image = image;

                        values.add(obbie);

                    }
                    catch (Exception ex) {
                        Log.d("lol", "CUNT");
                    }

                }

                return values;
            }

            return null;
        }

        catch (Exception ex) {
            Log.d("lol", ex.toString());
        }




        return null;
    }

    @Override
    public String getStory(String url) {
        // https://api.mangadex.org/manga/7bcdff73-7d1a-4c88-8501-58b027e1632c?includes[]=artist&includes[]=author&includes[]=cover_art
        String apiPage = "https://api.mangadex.org/manga/"+url.split("title/")[1]+"?includes[]=artist&includes[]=author&includes[]=cover_art";

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(apiPage)
                .build();
        String body;

        try (Response response = client.newCall(request).execute()) {
            body =  response.body().string();
        }
        catch (Exception ex) {
            Log.d("lol", ex.toString());
            return "Nothing";
        }

        try {
            if (!body.equals("")) {
                // Now we can start getting the fucking data!!

                // We'll store the objects with data inside this arraylist and return it later on
                ArrayList<ValuesForCollectDataPicScreen> values = new ArrayList<>();

                JSONObject jsonObject = new JSONObject(body);
                JSONObject data = jsonObject.getJSONObject("data");
                JSONObject attributes = data.getJSONObject("attributes");
                JSONObject description = attributes.getJSONObject("description");
                return description.getString("en");


            }
        }
        catch (Exception ex) {
            return "Nothing";
        }

        return "Nothing";
    }



    @Override
    public ArrayList<ValuesForChapters> GetChapters(String url){
        String id = url.split("title/")[1];
        String apiPage = "https://api.mangadex.org/manga/"+id+"/feed?limit=500" + // Limit 500 is the highest we can go
                "&includes[]=scanlation_group" +
                "&includes[]=user" +
                "&order[volume]=asc" +
                "&order[chapter]=asc" +
                "&offset=0" +
                "&contentRating[]=safe" +
                "&contentRating[]=suggestive" +
                "&contentRating[]=erotica" +
                "&contentRating[]=pornographic";

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(apiPage)
                .build();
        String body;
        try (Response response = client.newCall(request).execute()) {
            body =  response.body().string();
        }
        catch (Exception ex) {
            Log.d("lol", ex.toString());
            return null;
        }

        ArrayList<ValuesForChapters> data = new ArrayList<>();
        if (!body.equals("")) {
            try {
                JSONObject jsonObject = new JSONObject(body);
                JSONArray jsonArray = jsonObject.getJSONArray("data");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject ob = jsonArray.getJSONObject(i);
                    JSONObject attributes = ob.getJSONObject("attributes");
                    JSONArray relationships = ob.getJSONArray("relationships");
                    JSONObject one = relationships.getJSONObject(1);
                    String id2 = one.getString("id");


                    if (attributes.getString("translatedLanguage").equals("en")) {
                        // Now we get all of the data
                        ValuesForChapters valuesForChapters = new ValuesForChapters();
                        valuesForChapters.name = "Chapter "+attributes.getString("chapter");
                        valuesForChapters.url = "https://mangadex.org/chapter/"+ob.getString("id");
                        valuesForChapters.volume = attributes.getString("volume");
                        valuesForChapters.extraData.put("api_id", id2);

                        // Fixing bugs? No couldn't be me. We work around these things
                        if (attributes.getString("chapter") != null) {
                            data.add(valuesForChapters);
                        }

                    }
                }
            }
            catch (Exception ex) {
                Log.d("lol", ex.toString());

            }
        }
        return data;


    }

    @Override
    public ArrayList<String> GetImages(ValuesForChapters object, Context context) {
        String name = object.name.split(" ")[1]; // Chapter x -----> x
        String id = object.extraData.get("api_id");
        String apiPage = "https://api.mangadex.org/chapter" +
                "?manga="+id+
                "&volume="+object.volume+
                "&chapter="+name+
                "&contentRating[]=safe" +
                "&contentRating[]=suggestive" +
                "&contentRating[]=erotica" +
                "&contentRating[]=pornographic" +
                "&includes[]=scanlation_group" +
                "&includes[]=user";

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(apiPage)
                .build();
        String body;
        try (Response response = client.newCall(request).execute()) {
            body =  response.body().string();
        }
        catch (Exception ex) {
            Log.d("lol", ex.toString());
            return null;
        }

        // This is what we eventually want to achieve
        // https://uploads.mangadex.org/data/HASH/FILENAME

        try {
            JSONObject jsonObject = new JSONObject(body);
            JSONArray data = jsonObject.getJSONArray("data");
            ArrayList<String> images = new ArrayList<>();
            for (int i = 0; i < data.length(); i++) {
                JSONObject x = data.getJSONObject(i);
                JSONObject attributes = x.getJSONObject("attributes");

                if (attributes.getString("translatedLanguage").equals("en")) {
                    String hash = attributes.getString("hash");

                    JSONArray imagesLinks = attributes.getJSONArray("data");
                    for (int p = 0; p < imagesLinks.length(); p++) {
                        images.add("https://uploads.mangadex.org/data/"+hash+"/"+imagesLinks.getString(p));
                    }

                }
            }


            return images;


        }
        catch (Exception ex) {
            Log.d("lol", ex.toString());
        }



        return null;
    }

    @Override
    public HashMap<String, String> GetRequestData(String url) {
        HashMap<String,String> reqData = new HashMap<>();
        reqData.put("host", "uploads.mangadex.org");
        reqData.put("User-Agent", "Mozilla/5.0 (X11; Linux x86_64; rv:91.0) Gecko/20100101 Firefox/91.0");
        reqData.put("Referer", "");


        return reqData;
    }


    @Override
    public void PrepareReadChapter(ReadActivity readActivity) {
        // We don't really need to do anything so we can just leave this empty



    }
}
