package com.example.mangareader.Sources;

import android.content.Context;
import com.example.mangareader.Activities.ReadActivity;
import com.example.mangareader.Home.HomeMangaClass;
import com.example.mangareader.Settings.ListTracker;
import com.example.mangareader.Settings.Settings;
import com.example.mangareader.SourceHandlers.Sources;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

public class Mangadex implements Sources {

    private final OkHttpClient client;

    public Mangadex() {
        this.client = new OkHttpClient.Builder()
                .build();
    }

    private String GetBody(String url) {
        try {
            Request request = new Request.Builder()
                    .url(url)
                    .build();

            String body;
            try (Response response = client.newCall(request).execute()) {
                body = response.body().string();
                return body;
            }
        } catch (Exception ex) {
            return "";
        }

    }

    @Override
    public ArrayList<SearchValues> collectDataPicScreen(String manga) {
        // We first need to call the API
        try {
            String URL = "https://api.mangadex.org/manga?title=" + manga
                    + "&includes[]=cover_art&order[relevance]=desc";

            String body = GetBody(URL);

            if (!"".equals(body) && body != null) {
                // We'll store the objects with data inside this arraylist and return it later on
                ArrayList<SearchValues> values = new ArrayList<>();

                JSONObject jsonObject = new JSONObject(body);
                JSONArray jsonArray = jsonObject.getJSONArray("data");
                for (int i = 0; i < jsonArray.length(); i++) {

                    try {
                        JSONObject ob = jsonArray.getJSONObject(i);
                        String id = ob.getString("id");
                        String url = "https://mangadex.org/title/" + id; // ADD THIS

                        JSONObject x = ob.getJSONObject("attributes");
                        JSONObject y = x.getJSONObject("title");
                        String title = y.getString("en"); // ADD THIS

                        // Image link
                        // https://uploads.mangadex.org/covers/ID/FILENAME
                        JSONArray relationships = ob.getJSONArray("relationships");
                        JSONObject two = relationships.getJSONObject(2);
                        JSONObject attributes = two.getJSONObject("attributes");
                        String fileName = attributes.getString("fileName");
                        String image = "https://uploads.mangadex.org/covers/" + id + "/" + fileName;

                        SearchValues obbie = new SearchValues();
                        obbie.name = title;
                        obbie.url = url;
                        obbie.image = image;

                        values.add(obbie);

                    } catch (Exception ignored) {
                    }

                }

                return values;
            }

            return new ArrayList<>();
        } catch (Exception ignored) {
        }

        return new ArrayList<>();
    }

    @Override
    public String getStory(String url) {
        // An example:
        // https://api.mangadex.org/manga/7bcdff73-7d1a-4c88-8501-58b027e1632c?includes[]=artist&includes[]=author&includes[]=cover_art
        String apiPage = "https://api.mangadex.org/manga/" + url.split("title/")[1]
                + "?includes[]=artist&includes[]=author&includes[]=cover_art";

        String body = GetBody(apiPage);

        if ("".equals(body) || body == null) {
            return "Nothing";
        }

        try {
            JSONObject jsonObject = new JSONObject(body);
            JSONObject data = jsonObject.getJSONObject("data");
            JSONObject attributes = data.getJSONObject("attributes");
            JSONObject description = attributes.getJSONObject("description");
            return description.getString("en");

        } catch (Exception ex) {
            return "Nothing";
        }

    }

    private ArrayList<String> GetApiPageForChapters(String url, Context context) {
        String id = url.split("title/")[1];
        Settings settings = new Settings();
        Boolean allow_multiple_languages = settings.returnValueBoolean(context, "mangadex_preference_languages", false);
        ArrayList<String> links = new ArrayList<>();

        String initialPage = "";
        if (allow_multiple_languages) {
            initialPage = "https://api.mangadex.org/manga/" + id + "/feed?limit=500" +
                    "&includes[]=scanlation_group" +
                    "&includes[]=user" +
                    "&order[volume]=asc" +
                    "&order[chapter]=asc" +
                    "&offset=0" +
                    "&contentRating[]=safe" +
                    "&contentRating[]=suggestive" +
                    "&contentRating[]=erotica" +
                    "&contentRating[]=pornographic";
        } else {
            initialPage = "https://api.mangadex.org/manga/" + id + "/feed?limit=500" +
                    "&includes[]=scanlation_group" +
                    "&includes[]=user" +
                    "&order[volume]=asc" +
                    "&order[chapter]=asc" +
                    "&offset=0" +
                    "&contentRating[]=safe" +
                    "&contentRating[]=suggestive" +
                    "&contentRating[]=erotica" +
                    "&contentRating[]=pornographic" +
                    "&translatedLanguage[]=en";
        }

        Request request = new Request.Builder()
                .url(initialPage)
                .build();
        String body;
        try (Response response = client.newCall(request).execute()) {
            body = response.body().string();
        } catch (Exception ex) {
            return new ArrayList<>();
        }

        if (!body.equals("")) {

            try {
                JSONObject jsonObject = new JSONObject(body);
                double total = jsonObject.getDouble("total");

                if (total > 500) {
                    double amount = Math.ceil(total / 500);
                    for (int i = 0; i < amount; i++) {
                        StringBuilder page = new StringBuilder();
                        page.append("https://api.mangadex.org/manga/" + id + "/feed?limit=500" +
                                "&includes[]=scanlation_group" +
                                "&includes[]=user" +
                                "&order[volume]=asc" +
                                "&order[chapter]=asc" +
                                "&offset=" + i * 500 + "" +
                                "&contentRating[]=safe" +
                                "&contentRating[]=suggestive" +
                                "&contentRating[]=erotica" +
                                "&contentRating[]=pornographic");
                        if (!allow_multiple_languages) {
                            page.append("&translatedLanguage[]=en");
                        }
                        links.add(page.toString());

                    }
                } else {
                    links.add(initialPage);
                }
            } catch (Exception ignored) {
            }
        }

        return links;
    }

    @Override
    public ArrayList<ValuesForChapters> getChapters(String url, Context context, HashMap<String, Object> extraData) {

        ArrayList<String> api_pages = GetApiPageForChapters(url, context);
        ArrayList<ValuesForChapters> data = new ArrayList<>();
        if (api_pages != null) {
            for (String apiPage : api_pages) {

                String body = GetBody(apiPage);

                Settings settings = new Settings();
                Boolean allow_multiple_languages = settings.returnValueBoolean(context, "mangadex_preference_languages",
                        false);

                if (!"".equals(body) && body != null) {
                    try {
                        JSONObject jsonObject = new JSONObject(body);

                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject ob = jsonArray.getJSONObject(i);
                            JSONObject attributes = ob.getJSONObject("attributes");

                            // Now we get all of the data
                            ValuesForChapters valuesForChapters = new ValuesForChapters();

                            if (allow_multiple_languages) {
                                valuesForChapters.name = "[" + attributes.getString("translatedLanguage") + "]"
                                        + " Chapter " + attributes.getString("chapter");
                            } else {
                                valuesForChapters.name = "Chapter " + attributes.getString("chapter");
                            }

                            valuesForChapters.url = "https://mangadex.org/chapter/" + ob.getString("id");

                            // Fixing bugs? No, couldn't be me. I work around these things
                            if (!attributes.getString("chapter").toLowerCase(Locale.ROOT).equals("null")) {
                                data.add(valuesForChapters);
                            }
                        }
                    } catch (Exception ignored) {

                    }
                }
            }

        } else {
            return new ArrayList<>();
        }

        return data;

    }

    @Override
    public ArrayList<String> getImages(ValuesForChapters object, Context context) {
        String weird_ass_id = object.url.split("/")[4]; // Yes I really am doing it this way. I shouldn't need to do it this way...

        String apiPage = "https://api.mangadex.org/at-home/server/" + weird_ass_id;

        String body = GetBody(apiPage);

        if (body == null || "".equals(body)) {
            return new ArrayList<>();
        }

        try {
            JSONObject jsonObject = new JSONObject(body);
            JSONObject chapter = jsonObject.getJSONObject("chapter");
            String hash = chapter.getString("hash");
            JSONArray data = chapter.getJSONArray("data");
            ArrayList<String> images = new ArrayList<>();
            for (int i = 0; i < data.length(); i++) {
                // This is what the url should look like:
                // https://uploads.mangadex.org/data/1e092cc6e1300bcf18dd5a5f5c8c1f56/x3-abccc0fe61f05aa5e3dca13515f14dc8291d63e7a4a6e6e59294dab7813dac42.jpg

                String url_last_part = data.getString(i);
                String url = "https://uploads.mangadex.org/data/" + hash + "/" + url_last_part;
                images.add(url);

            }

            return images;

        } catch (Exception ignored) {
        }

        return new ArrayList<>();
    }

    @Override
    public HashMap<String, String> getRequestData(String url) {
        HashMap<String, String> reqData = new HashMap<>();
        reqData.put("host", "uploads.mangadex.org");
        reqData.put("User-Agent", "Mozilla/5.0 (X11; Linux x86_64; rv:91.0) Gecko/20100101 Firefox/91.0");
        reqData.put("Referer", "");
        reqData.put("noRefererWhenDownloading", "true"); // When downloading an image we want the referer to be empty

        return reqData;
    }

    @Override
    public void prepareReadChapter(ReadActivity readActivity) {
        // We don't really need to do anything, so we can just leave this empty
    }

    /*
     * I've been having a lot of trouble with mangadex
     * Basically getting all the data from the API is HELL
     * It's just a very slow process because you have to get multiple APIs for all
     * the necessary data
     * Things I have implemented to make everything faster:
     * - I added a cache that remember all the manga that have been on the home screen before
     * - I added an allow and deny list that remembers which manga have been blocked because they have no chapters
     * - I added threading
     */
    @Override

    public HashMap<String, ArrayList<HomeMangaClass>> getDataHomeActivity(Context context) throws InterruptedException {

        HashMap<String, ArrayList<HomeMangaClass>> data = new HashMap<>();

        // Mangadex hardcodes this and they use JS, so I cannot webscrape either
        // I guess I could use like a headless browser but I cba
        String URL_SEASONAL = "https://api.mangadex.org/list/7df1dabc-b1c5-4e8e-a757-de5a2a3d37e9?includes[]=user";

        String URL_LATEST = "https://api.mangadex.org/chapter?limit=100&offset=0&includes[]=user&includes[]=scanlation_group&includes[]=manga&contentRating[]=safe&contentRating[]=suggestive&contentRating[]=erotica&order[readableAt]=desc";

        final String[] body = new String[1];

        Request request;

        ArrayList<HomeMangaClass> seasonalObjects = new ArrayList<>();
        ArrayList<HomeMangaClass> latestObjects = new ArrayList<>();

        // Gets the popular/seasonal manga
        request = new Request.Builder()
                .url(URL_SEASONAL)
                .build();

        Thread t1 = null;
        Thread t2 = null;

        try (Response response = client.newCall(request).execute()) {
            body[0] = response.body().string();

            JSONObject jsonObject = new JSONObject(body[0]);
            JSONObject data1 = jsonObject.getJSONObject("data");

            JSONArray relationships = data1.getJSONArray("relationships");
            for (int i = 0; i < relationships.length(); i++) {
                int finalI = i;
                t1 = new Thread(() -> {
                    JSONObject number = null;
                    String id = null;
                    try {
                        number = relationships.getJSONObject(finalI);
                        id = number.getString("id");

                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }

                    String seasonal_url = "https://mangadex.org/title/" + id;

                    GetDataHomeActivityImageAndTitle x = GetImageAndTitle(id, seasonal_url, context);

                    if (x != null) {
                        seasonalObjects.add(new HomeMangaClass(x.title, seasonal_url, x.image, null));
                    }
                });
                t1.start();
            }
        } catch (Exception exception) {
            return new HashMap<>();
        }

        //NOW WE GET THE LATEST MANGA
        ArrayList<String> ids = new ArrayList<>();

        request = new Request.Builder()
                .url(URL_LATEST)
                .build();
        try (Response response = client.newCall(request).execute()) {
            body[0] = response.body().string();
            JSONObject jsonObject = new JSONObject(body[0]);
            JSONArray data_array = jsonObject.getJSONArray("data");

            for (int i = 0; i < data_array.length(); i++) {
                try {
                    int finalI = i;
                    t2 = new Thread(() -> {
                        try {
                            JSONObject number = data_array.getJSONObject(finalI);
                            JSONArray relationships = number.getJSONArray("relationships");
                            JSONObject one = relationships.getJSONObject(1);
                            String id = one.getString("id");

                            if (!ids.contains(id)) {
                                ids.add(id);
                                String latestUrl = "https://mangadex.org/title/" + id;

                                GetDataHomeActivityImageAndTitle x = GetImageAndTitle(id, latestUrl, context);
                                if (x != null) {
                                    latestObjects.add(new HomeMangaClass(x.title, latestUrl, x.image, null));
                                }
                            }
                        } catch (Exception ignored) {
                        }

                    });
                    t2.start();
                } catch (Exception ignored) {

                }
            }
        } catch (Exception ex) {
            return new HashMap<>();
        }

        t1.join();
        t2.join();

        data.put("popular", seasonalObjects);
        data.put("latest", latestObjects);

        return data;
    }

    @Override
    public String getSourceName() {
        return "mangadex";
    }

    // I am absolutely NOT happy with the way this is working right now
    // I really really want to change this in the future but for now this will suffice
    GetDataHomeActivityImageAndTitle GetImageAndTitle(String id, String url, Context context) {

        Settings settings = new Settings();
        boolean mangadex_preference_languages = settings.returnValueBoolean(context, "mangadex_preference_languages",
                false);

        ArrayList<String> stuff = new ArrayList<>();

        if (mangadex_preference_languages) {
            stuff = ListTracker.getFromList(context, "home_mangadex_cache_multilingual");
        } else {
            stuff = ListTracker.getFromList(context, "home_mangadex_cache_english");
        }

        for (String i : stuff) {
            try {
                JSONObject manga = new JSONObject(i);

                if (manga.getString("url").equals(url)) {
                    return new GetDataHomeActivityImageAndTitle(manga.getString("name"), manga.getString("image"));
                }
            } catch (Exception ignored) {
            }
        }

        String newUrl = "https://api.mangadex.org/manga/" + id
                + "?includes[]=artist&includes[]=author&includes[]=cover_art";

        Request request = new Request.Builder()
                .url(newUrl)
                .build();

        String body;
        try (Response response = client.newCall(request).execute()) {
            body = response.body().string();

            JSONObject jsonObject = new JSONObject(body);
            if (jsonObject.has("data")) {
                JSONObject data1 = jsonObject.getJSONObject("data");
                JSONObject attributes = data1.getJSONObject("attributes");
                JSONObject titlee = attributes.getJSONObject("title");
                String title = "[Unknown title]";
                if (titlee.has("en")) {
                    title = titlee.getString("en");
                } else {
                    if (titlee.has("ja")) {
                        title = titlee.getString("ja");
                    }
                }

                JSONArray relationships2 = data1.getJSONArray("relationships");
                JSONObject two = relationships2.getJSONObject(2);
                attributes = two.getJSONObject("attributes");

                // ------------------------------------------------------------------------------------------------------------------------------------------------
                // Let me explain what is going on here
                // On mangadex some mangas might be empty (I don't know why), they do not
                // contain any chapters
                // We can hide these "faulty" manga by getting the chapter list and checking the
                // amount of chapters (we check if it's bigger than 0)
                // The problem is that this is a very, very slow process
                // So I came up with the idea to remember which manga are faulty and which are not
                // We also want to differentiate between the English manga and the non-English manga (see settings)
                // On top of that we want to keep a cache of manga we have already seen so next
                // time we can zoom through the whole process.
                // -------------------------------------------------------------------------------------------------------------------------------------------------
                if (attributes.has("fileName")) {

                    ArrayList<String> allowList;
                    ArrayList<String> denyList;

                    if (mangadex_preference_languages) {
                        allowList = ListTracker.getFromList(context, "allowList_multilingual_mangadex");
                        denyList = ListTracker.getFromList(context, "denyList_multilingual_mangadex");
                    } else {
                        allowList = ListTracker.getFromList(context, "allowList_english_mangadex");
                        denyList = ListTracker.getFromList(context, "denyList_english_mangadex");
                    }

                    if (allowList.contains(url)) {
                        String imageUrl = "https://mangadex.org/covers/" + id + "/" + attributes.getString("fileName");

                        AddMangaToListtracker(mangadex_preference_languages, context, title, id, imageUrl);

                        return new GetDataHomeActivityImageAndTitle(title, imageUrl);
                    } else {
                        if (!denyList.contains(url)) {
                            ArrayList<ValuesForChapters> y = getChapters(url, context, null);
                            if (y.size() != 0) { // Makes sure there is nothing empty but makes the whole process A LOT
                                // slower.
                                String imageUrl = "https://mangadex.org/covers/" + id + "/"
                                        + attributes.getString("fileName");

                                if (mangadex_preference_languages) {
                                    AddMangaToListtracker(true, context, title, id, imageUrl);

                                    ListTracker.addToList(context, url, "allowList_multilingual_mangadex");

                                } else {
                                    ListTracker.addToList(context, url, "allowList_english_mangadex");
                                    AddMangaToListtracker(false, context, title, id, imageUrl);

                                }

                                return new GetDataHomeActivityImageAndTitle(title, imageUrl);

                            } else {
                                if (mangadex_preference_languages) {
                                    ListTracker.addToList(context, url, "denyList_multilingual_mangadex");
                                } else {
                                    ListTracker.addToList(context, url, "denyList_english_mangadex");

                                }

                            }
                        }
                    }

                }
            }
        } catch (Exception ex) {
            return null;
        }

        return null;
    }

    void AddMangaToListtracker(boolean mangadex_preference_languages, Context context, String title, String id,
                               String imageUrl) {
        try {
            String jsonString = new JSONObject()
                    .put("name", title)
                    .put("url", "https://mangadex.org/title/" + id)
                    .put("image", imageUrl)
                    .toString();

            if (mangadex_preference_languages) {
                ListTracker.addToList(context, jsonString, "home_mangadex_cache_multilingual");
            } else {
                ListTracker.addToList(context, jsonString, "home_mangadex_cache_english");
            }
        } catch (Exception ignored) {
        }

    }

    static class GetDataHomeActivityImageAndTitle {
        String image;
        String title;

        GetDataHomeActivityImageAndTitle(String title, String image) {
            this.image = image;
            this.title = title;
        }
    }

}
