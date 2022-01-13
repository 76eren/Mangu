package com.example.mangareader.Sources;

import android.content.Context;
import android.util.Log;
import com.example.mangareader.Activities.ReadActivity;
import com.example.mangareader.Home.HomeMangaClass;
import com.example.mangareader.Settings;
import com.example.mangareader.SourceHandlers.Sources;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

public class Mangadex implements Sources {
    @Override
    public ArrayList<SearchValues> CollectDataPicScreen(String manga) {
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
                ArrayList<SearchValues> values = new ArrayList<>();

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

                        SearchValues obbie = new SearchValues();
                        obbie.name = title;
                        obbie.url = url;
                        obbie.image = image;

                        values.add(obbie);

                    }

                    catch (Exception ex) {
                    }

                }

                return values;
            }

            return null;
        }

        catch (Exception ex) {
        }


        return null;
    }

    @Override
    public String getStory(String url) {
        // An example:
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
            return "Nothing";
        }

        try {
            if (!body.equals("")) {
                // Now we can start getting the fucking data!!

                // We'll store the objects with data inside this arraylist and return it later on
                ArrayList<SearchValues> values = new ArrayList<>();

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
    public ArrayList<ValuesForChapters> GetChapters(String url, Context context){
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
        Settings settings = new Settings();
        Boolean allow_multiple_languages = settings.ReturnValueBoolean(context, "mangadex_preference_languages", false);

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


                    if (!allow_multiple_languages && !attributes.getString("translatedLanguage").equals("en")) {
                        continue;
                    }

                    // Now we get all of the data
                    ValuesForChapters valuesForChapters = new ValuesForChapters();

                    if (allow_multiple_languages) {
                        valuesForChapters.name = "["+attributes.getString("translatedLanguage")+"]"+ " Chapter "+attributes.getString("chapter");
                    }
                    else {
                        valuesForChapters.name = "Chapter "+attributes.getString("chapter");
                    }

                    valuesForChapters.url = "https://mangadex.org/chapter/"+ob.getString("id");


                    // Fixing bugs? No couldn't be me. I work around these things
                    if (!attributes.getString("chapter").toLowerCase(Locale.ROOT).equals("null")) {
                        data.add(valuesForChapters);
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
        String weird_ass_id = object.url.split("/")[4]; // Yes I really am doing it this way. I shouldn't fucking need to do it this way...

        String apiPage = "https://api.mangadex.org/at-home/server/"+weird_ass_id;

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(apiPage)
                .build();
        String body;
        try (Response response = client.newCall(request).execute()) {
            body =  response.body().string();
        }
        catch (Exception ex) {
            return null;
        }

        try {
            JSONObject jsonObject = new JSONObject(body);
            JSONObject chapter = jsonObject.getJSONObject("chapter");
            String hash = chapter.getString("hash");
            JSONArray data = chapter.getJSONArray("data");
            ArrayList<String> images = new ArrayList<>();
            for (int i = 0; i < data.length(); i++) {
                // This is what a url should look like: https://uploads.mangadex.org/data/1e092cc6e1300bcf18dd5a5f5c8c1f56/x3-abccc0fe61f05aa5e3dca13515f14dc8291d63e7a4a6e6e59294dab7813dac42.jpg

                String url_last_part = data.getString(i);
                String url = "https://uploads.mangadex.org/data/"+hash+"/"+url_last_part;
                Log.d("lol", url);
                images.add(url);

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

    @Override
    public HashMap<String, ArrayList<HomeMangaClass>> GetDataHomeActivity() {
        HashMap<String, ArrayList<HomeMangaClass>> data = new HashMap<>();


        String URL_SEASONAL = "https://api.mangadex.org/manga?limit=100&offset=0&includes[]=cover_art&includes[]=author&includes[]=artist&contentRating[]=safe&contentRating[]=suggestive&contentRating[]=erotica&order[relevance]=desc&ids[]=f721d5ed-7667-4ff5-8a2f-5e46afaea7d6&ids[]=26937e2f-de0e-498f-9295-7eed381b88b3&ids[]=7e2933d2-8587-4c0f-b69c-4233864b96a8&ids[]=151d1758-f871-46ac-8edc-75ab69b688fe&ids[]=b6b87e4d-c1cc-4d0b-8588-df715dd98624&ids[]=563355c9-65cc-4011-8665-15bdc013d00a&ids[]=975f3334-8395-4393-84a2-50fcaccbcdc0&ids[]=24c0f6c0-ec09-4b83-ab52-a855283bb6e3&ids[]=e3d2897c-ca52-4675-ae0b-0af1bfe534dd&ids[]=4e166e00-a9b3-4f17-8075-441b9745715a&ids[]=4c05b1b4-7da6-4e7a-ae39-5cc2363093f0&ids[]=55a96d76-10a2-4c4b-bc60-3a4aa8325e50&ids[]=aa6c76f7-5f5f-46b6-a800-911145f81b9b&ids[]=c8aebcc7-678e-4682-a727-48febbc325fd&ids[]=70fe972b-3c88-4165-b9a5-3db96d6d2c14&ids[]=1ef6ddce-7930-45ae-a335-9a45604b99f7&ids[]=62608082-d81d-4513-af7d-ae3ffe2423d2&ids[]=770c61b9-0ef2-460b-8c25-c10ab23349ce&ids[]=7433b48f-0bb1-4b72-b88d-07b2939db1ff&ids[]=b603c211-d1f9-4145-a2ac-04dcd5f06329&ids[]=10227f74-9134-4262-b01d-8966bc149c4d&ids[]=308f0355-fedb-4861-a4e0-56fe35ae61d3&ids[]=789642f8-ca89-4e4e-8f7b-eee4d17ea08b&ids[]=248525ed-ad1c-4ddc-a834-5d6ce66a3ad2&ids[]=e1d8b192-fa28-4651-bdaf-d4e0d4418ea8&ids[]=dc34064e-3cd4-48c4-9ec5-550845b96ffb&ids[]=6e445564-d9a8-4862-bff1-f4d6be6dba2c&ids[]=808fdeae-5ea2-4839-8f95-8183dfdddddc&ids[]=b30540c7-bde6-4279-9645-a3eddc5c9e97&ids[]=5ce0d9df-a3cc-421e-bc33-796869b6b9f7&ids[]=304ceac3-8cdb-4fe7-acf7-2b6ff7a60613";
        String URL_LATEST = "https://api.mangadex.org/manga?includes[]=cover_art&contentRating[]=safe&contentRating[]=suggestive&contentRating[]=erotica&contentRating[]=pornographic&ids[]=7960b151-f8a2-4143-bfa7-81e870e0f6a4&ids[]=d86c90aa-7cfb-455a-99f7-37ad6d6a6eb2&ids[]=d7037b2a-874a-4360-8a7b-07f2899152fd&ids[]=885286e9-451e-4def-86f0-9ae0965813ad&ids[]=c5d2eec8-7ed0-4d7d-b54c-b686f30cdcd5&ids[]=425f2ccf-581f-42cf-aed3-c3312fcde926&ids[]=5c9716ba-acdf-4592-bade-c2f1e1a74eae&ids[]=55c89489-90bd-4f14-8340-82bceea97752&ids[]=caae74bc-c055-4f25-883d-102141eeae62&ids[]=b2e70386-d645-4fea-b8d2-e9749ca2f196&ids[]=d2df017b-c003-4de6-9625-4f1fba7aef97&ids[]=cb00804f-49a7-41ad-bc4f-11054c72ead1&ids[]=5678f637-db18-4d81-b416-e871513fc598&ids[]=fbfa798a-5172-4397-b8bd-a5bb0b6f15e9&ids[]=308c43cf-1e52-4e3d-a048-56524aab1b27&ids[]=edaae213-67c7-4a6c-ad5f-141001891741&ids[]=b3a38ab3-2509-4a4c-a4b4-5c9f2e2db0ab&ids[]=6fe280d6-d419-4aae-ba2c-cb3df5ef8da7&ids[]=cc21c9a8-3c44-421d-94a3-10e0b82fdfd3&ids[]=87ffa375-bd2c-49ba-ba0c-6d78ea07c342&ids[]=60449946-9a58-4274-a566-fbe4235414aa&ids[]=683a613e-8aa0-4b98-9a62-4138463d6a49&ids[]=5209fe10-4a14-403f-8837-2ccf8cced253&ids[]=94891715-a109-4f5e-81ef-2bed5fb5bb19&ids[]=8a38a197-949e-4f83-90cd-5ee6b9ed36d3&ids[]=68b03ba8-bf66-436b-88d0-bb8c88114fe8&limit=100";
        String body;
        ArrayList<HomeMangaClass> seasonal = new ArrayList<>();
        ArrayList<HomeMangaClass> latest = new ArrayList<>();

        OkHttpClient client = new OkHttpClient();
        Request request = null;

        // I fucking hate how I decided to approach this situation
        // We can use the same code for the latest and the popular (seasonal)
        // Theres definitely a better way of doing this though...
        for (int q = 0; q < 2; q++) {
            if (q == 0) {
                request = new Request.Builder()
                        .url(URL_SEASONAL)
                        .build();
            }

            if (q == 1){
                request = new Request.Builder()
                        .url(URL_LATEST)
                        .build();
            }

            try (Response response = client.newCall(request).execute()) {
                body =  response.body().string();
            }
            catch (Exception ex) {
                return null;
            }

            // Gets all of the data
            try {
                JSONObject jsonObject = new JSONObject(body);
                JSONArray dataArray = jsonObject.getJSONArray("data");

                for (int i = 0; i < dataArray.length(); i++) {
                    JSONObject x = dataArray.getJSONObject(i);
                    JSONObject attributes = x.getJSONObject("attributes");
                    JSONObject title = attributes.getJSONObject("title");

                    String name;
                    if (!title.has("en")) {
                        if (title.has("ja")) {
                            name = title.getString("ja");
                        }
                        else {
                            continue;
                        }
                    }
                    else {
                        name = title.getString("en"); // THIS IS THE TITLE OF THE MANGA
                    }

                    String id = x.getString("id");
                    String mangaUrl = "https://mangadex.org/title/"+id; // THE URL TO THE MANGA CHAPTER LIST

                    JSONArray relationships = x.getJSONArray("relationships");

                    String image = "";
                    for (int y = 0; y < relationships.length(); y++) {
                        JSONObject z = relationships.getJSONObject(y);
                        String type = z.getString("type");
                        if (type.equals("cover_art")) {
                            JSONObject attributes_two = z.getJSONObject("attributes");

                            String filename = attributes_two.getString("fileName");
                            image = "https://uploads.mangadex.org/covers/"+id+"/"+filename; // THE URL TO THE IMAGE
                            break;
                        }
                    }

                    if (q == 0) {
                        seasonal.add(new HomeMangaClass(name, mangaUrl, image));
                    }
                    if (q == 1) {
                        latest.add(new HomeMangaClass(name, mangaUrl, image));
                    }
                }

                if (q == 0) {
                    data.put("popular", seasonal); // This isn't misleading or anything
                }
                if (q == 1) {
                    data.put("latest", latest);
                }
            }
            catch (Exception ex) {
                Log.d("lol", ex.toString());
                return null;
            }

        }


        return data;
    }
}
