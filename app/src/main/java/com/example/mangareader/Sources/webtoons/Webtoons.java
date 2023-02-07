// I would like to thank ipsvn for helping me out with a lot of things going on here

package com.example.mangareader.Sources.webtoons;

import android.content.Context;
import android.util.Log;
import com.example.mangareader.Activities.ReadActivity;
import com.example.mangareader.Home.HomeMangaClass;
import com.example.mangareader.SourceHandlers.Sources;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class Webtoons implements Sources {
    private String GetBody(String url) {
        try {
            Request request = new Request.Builder()
                    .url(url)
                    .build();

            String body;
            OkHttpClient client = new OkHttpClient();
            try (Response response = client.newCall(request).execute()) {
                body = response.body().string();
                return body;
            }
        } catch (Exception ex) {
            Log.d("error", ex.toString());
            return null;
        }

    }

    @Override
    public ArrayList<SearchValues> CollectDataPicScreen(String manga) {
        manga = manga.trim();
        manga = manga.replace("\\s", "%20");
        String url = "https://www.webtoons.com/en/search?keyword=" + manga;

        Document document;

        try {
            document = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (X11; Linux x86_64; rv:103.0) Gecko/20100101 Firefox/103.0")
                    .get();
        } catch (Exception ex) {
            Log.d("error", ex.toString());
            return null;
        }

        ArrayList<SearchValues> data = new ArrayList<>();
        data.addAll(SearchDataCollector(document, "card_lst"));
        data.addAll(SearchDataCollector(document, "challenge_lst"));


        return data;
    }

    // Is called by the CollectDataPicScreen function, so we can just re-use code
    ArrayList<SearchValues> SearchDataCollector(Document document, String initialClassName) {
        // We get both the card_lst and the challange_list
        ArrayList<SearchValues> data = new ArrayList<>();

        // We start with the card_lst
        Elements card_lst = document.getElementsByClass(initialClassName);
        Elements li = card_lst.select("li");
        for (Element i : li) {
            Elements a = i.select("a");
            String chapterUrl = "https://www.webtoons.com" + a.attr("href"); // url

            Elements src = i.select("img");
            String image = src.attr("src"); // image
            String name = src.attr("alt"); // name

            SearchValues searchValues = new SearchValues();
            searchValues.referer = "webtoons.com/";
            searchValues.image = image;
            searchValues.url = chapterUrl;
            searchValues.name = name;

            data.add(searchValues);
        }
        return data;
    }

    @Override
    public String getStory(String url) {
        Document doc;
        try {
            doc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (X11; Linux x86_64; rv:103.0) Gecko/20100101 Firefox/103.0")
                    .get();
        } catch (Exception ex) {
            return "A problem occured whilst trying to get the description";
        }

        return doc.getElementsByClass("summary").text();
    }

    @Override
    public ArrayList<ValuesForChapters> GetChapters(String url, Context context, HashMap<String, Object> extraData) throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException, JSONException {
        // I am actually surprised this function works
        url = url.toLowerCase(Locale.ROOT);
        String old_url = url;
        String title = (String) extraData.get("mangaName");
        String genre = "";

        String id = url.split("no=")[1];
        if (!url.contains("challenge") && !url.contains("episodelist")) {

            genre = url.split("en/")[1];
            genre = genre.split("/")[0];
        } else {
            // We first need to get the full url

            String body = "";
            String apiUrl = WebtoonsMac.GetChapterList("https://apis.webtoons.com/lineWebtoon/webtoon/titleList.json?v=2&platform=APP_ANDROID&language=en&locale=en&serviceZone=GLOBAL&query=un&startIndex=0&pageSize=1663334080258");
            body = GetBody(apiUrl);


            if (!Objects.equals(body, "")) {
                JSONObject jsonObject = new JSONObject(body);
                JSONObject message = jsonObject.getJSONObject("message");
                JSONObject result = message.getJSONObject("result");
                JSONObject titleList = result.getJSONObject("titleList");
                JSONArray titles = titleList.getJSONArray("titles");
                boolean got = false;
                for (int i = 0; i < titles.length(); i++) {
                    JSONObject a = titles.getJSONObject(i);
                    String titleNo = a.getString("titleNo");
                    if (titleNo.equals(id)) {
                        genre = a.getString("representGenre");
                        got = true;
                        break;
                    }
                }
                if (!got) {
                    // Surely this can't go wrong :))
                    genre = "challenge";
                }

            } else {
                return null;
            }
        }

        String api_page;
        if (!old_url.contains("challenge")) {
            api_page = WebtoonsMac.GetChapterList("https://apis.webtoons.com/lineWebtoon/webtoon/episodeList.json?language=en&locale=en&serviceZone=GLOBAL&titleNo=" + id + "&v=4&platform=APP_ANDROID&titleNo=95&startIndex=0&pageSize=100000");
        } else {
            api_page = WebtoonsMac.GetChapterList("https://apis.webtoons.com/lineWebtoon/webtoon/challengeEpisodeList.json?language=en&locale=en&serviceZone=GLOBAL&titleNo=" + id + "&v=4&platform=APP_ANDROID&titleNo=95&startIndex=0&pageSize=100000");
        }

        String body = GetBody(api_page);
        ArrayList<ValuesForChapters> data = new ArrayList<>();

        if (!body.equals("")) {
            JSONObject jsonObject = new JSONObject(body);
            JSONObject message = jsonObject.getJSONObject("message");
            JSONObject result = message.getJSONObject("result");
            JSONObject episodeList = result.getJSONObject("episodeList");
            JSONArray episode = episodeList.getJSONArray("episode");

            for (int i = 0; i < episode.length(); i++) {
                JSONObject ob = episode.getJSONObject(i);
                String episodeTitle = ob.getString("episodeTitle");
                String initialTitle = episodeTitle;

                // Actually working around bugs
                episodeTitle = episodeTitle.replaceAll("[^a-zA-Z0-9]", " ");
                episodeTitle = episodeTitle.replace(" ", "-");
                episodeTitle = episodeTitle.replace("---", "-");
                episodeTitle = episodeTitle.replace("--", "-");
                episodeTitle = episodeTitle.replace("----", "-");

                int titleNo = ob.getInt("titleNo");
                int episodeNo = ob.getInt("episodeNo");

                String chapterUrl;
                if (!old_url.contains("challenge")) {
                    chapterUrl = "https://www.webtoons.com/en/" + genre + "/" + title + "/" + episodeTitle + "/viewer?title_no=" + titleNo + "&episode_no=" + episodeNo;
                } else {
                    genre = "challenge";
                    chapterUrl = "https://www.webtoons.com/en/challenge/" + title + "/" + episodeTitle + "/viewer?title_no=" + id + "&episode_no=" + episodeNo;
                }

                ValuesForChapters valuesForChapters = new ValuesForChapters();
                valuesForChapters.name = initialTitle;
                valuesForChapters.url = chapterUrl;
                valuesForChapters.extraData = null;
                data.add(valuesForChapters);
            }
        }

        Collections.reverse(data);
        return data;
    }

    @Override
    public ArrayList<String> GetImages(ValuesForChapters object, Context context) {
        ArrayList<String> images = new ArrayList<>();

        String titleNo = object.url.split("title_no=")[1];
        titleNo = titleNo.split("&")[0];

        String episodeNo = object.url.split("episode_no=")[1];

        String apiUrl;
        if (!object.url.toLowerCase().contains("challenge")) {
            apiUrl = "https://apis.webtoons.com/lineWebtoon/webtoon/episodeInfo.json?language=en&locale=en&serviceZone=GLOBAL&titleNo=" + titleNo + "&v=4&platform=APP_ANDROID&titleNo=" + titleNo + "&episodeNo=" + episodeNo;
        } else {
            apiUrl = "https://apis.webtoons.com/lineWebtoon/webtoon/challengeEpisodeInfo.json?language=en&locale=en&serviceZone=GLOBAL&titleNo=" + titleNo + "&v=4&platform=APP_ANDROID&titleNo=" + titleNo + "&episodeNo=" + episodeNo;
        }

        String apiPage = "";
        try {
            apiPage = WebtoonsMac.GetChapterList(apiUrl);
        } catch (Exception ex) {
            Log.d("error", ex.toString());
        }
        String body = GetBody(apiPage);

        if (!body.equals("")) {
            try {
                JSONObject jsonObject = new JSONObject(body);
                JSONObject message = jsonObject.getJSONObject("message");
                JSONObject result = message.getJSONObject("result");
                JSONObject episodeInfo = result.getJSONObject("episodeInfo");
                JSONArray imageInfo = episodeInfo.getJSONArray("imageInfo");
                for (int i = 0; i < imageInfo.length(); i++) {
                    JSONObject x = imageInfo.getJSONObject(i);
                    String url = x.getString("url");
                    url = "https://webtoon-phinf.pstatic.net" + url;
                    images.add(url);
                }

            } catch (Exception ex) {
                Log.d("error", ex.toString());
            }
        }

        return images;
    }

    @Override
    public HashMap<String, String> GetRequestData(String url) {
        HashMap<String, String> reqData = new HashMap<>();
        reqData.put("User-Agent", "Mozilla/5.0 (X11; Linux x86_64; rv:91.0) Gecko/20100101 Firefox/91.0");
        reqData.put("Referer", "https://www.webtoons.com/");

        return reqData;
    }

    @Override
    public void PrepareReadChapter(ReadActivity readActivity) {

    }

    @Override
    public HashMap<String, ArrayList<HomeMangaClass>> GetDataHomeActivity(Context context) throws InterruptedException {
        HashMap<String, ArrayList<HomeMangaClass>> data = new HashMap<>();

        String latest_url = "https://www.webtoons.com/en/dailySchedule?sortOrder=UPDATE&webtoonCompleteType=ONGOING";
        String popular_url = "https://www.webtoons.com/en/dailySchedule?sortOrder=READ_COUNT&webtoonCompleteType=ONGOING";

        Thread t1;
        Thread t2;
        t1 = new Thread(() -> {
            ArrayList<HomeMangaClass> latest_list = GetHomescreenData(latest_url);
            data.put("latest", latest_list);
        });
        t2 = new Thread(() -> {
            ArrayList<HomeMangaClass> popular_list = GetHomescreenData(popular_url);
            data.put("popular", popular_list);
        });

        t1.start();
        t2.start();

        t1.join();
        t2.join();

        return data;
    }

    @Override
    public String GetSourceName() {
        return "webtoons";
    }

    // Gets called by GetDataHomeScreen, so we don't have to repeat code
    private ArrayList<HomeMangaClass> GetHomescreenData(String url) {
        Document document;
        try {
            document = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (X11; Linux x86_64; rv:103.0) Gecko/20100101 Firefox/103.0")
                    .get();
        } catch (Exception ex) {
            Log.d("error", ex.toString());
            return null;
        }

        // We get the latest first
        Calendar calendar = Calendar.getInstance();
        int day_int = calendar.get(Calendar.DAY_OF_WEEK);
        String day = "";

        switch (day_int) {
            // The first day of the week is Monday and I do not care what anybody says
            case 2:
                day = "Monday";
                break;
            case 3:
                day = "Tuesday";
                break;
            case 4:
                day = "Wednesday";
                break;
            case 5:
                day = "Thursday";
                break;
            case 6:
                day = "Friday";
                break;
            case 7:
                day = "Saturday";
                break;
            case 1:
                day = "Sunday";
                break;

        }
        ArrayList<HomeMangaClass> list = new ArrayList<>();
        String class_name = "_list_" + day.toUpperCase(Locale.ROOT);
        Elements daily_section_on_list = document.getElementsByClass(class_name);
        Elements ul = daily_section_on_list.select("ul");
        Elements li = ul.select("li");
        for (int i = 0; i < li.size(); i++) {
            String name = li.get(i).getElementsByClass("subj").text(); // The name of the manga

            Elements daily_card = li.get(i).getElementsByClass("daily_card_item");
            String chapterUrl = daily_card.attr("href"); // The url to the chapter

            Elements img = daily_card.select("img");
            String imageUrl = img.attr("src");
            imageUrl = imageUrl.replace("a138", "a306"); // This might go wrong at some point tbh. Basically this tries to get a higher res image
            list.add(new HomeMangaClass(name, chapterUrl, imageUrl, "https://www.webtoons.com/"));
        }
        return list;

    }

}
