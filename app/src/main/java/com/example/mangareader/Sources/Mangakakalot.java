package com.example.mangareader.Sources;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import androidx.appcompat.widget.SwitchCompat;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.example.mangareader.Activities.ReadActivity;
import com.example.mangareader.Home.HomeMangaClass;
import com.example.mangareader.Settings;
import com.example.mangareader.SourceHandlers.Sources;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Safelist;
import org.jsoup.select.Elements;
import com.example.mangareader.R;

import java.util.*;

public class Mangakakalot implements Sources {

    private static final Document.OutputSettings NO_PRETTY_PRINTING = new Document.OutputSettings().prettyPrint(false);

    // public Context context;

    // THIS IS FOR THE MAIN ACTIVITY
    // GETS THE INFO FOR THE PICTURE SCREEN
    @Override
    public ArrayList<SearchValues> CollectDataPicScreen(String manga) {

        manga = manga.replace(" ", "_");
        String url = "https://mangakakalot.com/search/story/" + manga;
        Document doc;
        try {
            doc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (X11; Linux x86_64; rv:89.0) Gecko/20100101 Firefox/89.0")
                    .get();
        } catch (Exception ex) {
            return null;
        }

        Elements elems = doc.getElementsByClass("panel_story_list");
        ArrayList<SearchValues> objectsPicScreen = new ArrayList<>();
        if (elems.first() != null) {
            for (Element i : elems.first().children()) {
                Elements x = i.getElementsByClass("story_item");
                Elements y = x.select("a");
                Elements z = y.select("img");

                String img = z.attr("src"); // A link to the image
                String link = y.attr("href"); // A link to the chapter list
                String name = z.attr("alt"); // The manga's name

                // Now we need to add our stuff to the arrays

                SearchValues object = new SearchValues();
                object.image = img;
                object.name = name;
                object.url = link;

                objectsPicScreen.add(object);

            }

            return objectsPicScreen;

        }

        return null;
    }

    Document doc;

    @Override
    public String getStory(String url) {
        try {
            doc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (X11; Linux x86_64; rv:89.0) Gecko/20100101 Firefox/89.0")
                    .get();

            // MANGAKAKLOT USES MULTIPLE WEBSITES
            Elements x = null;
            if (url.toLowerCase().contains("readmanganato.com")) {
                x = doc.getElementsByClass("panel-story-info-description"); // This also contains data we don't need
            } else if (url.toLowerCase().contains("mangakakalot.com")) {
                x = doc.select("div#noidungm");
            }

            // I stole this from somewhere on stackoverflow

            if (x != null) {
                Document document = Jsoup.parse(x.html());
                document.outputSettings(NO_PRETTY_PRINTING); // makes html() preserve
                // linebreaks and spacing
                document.select("br").append("\\n");
                document.select("p").prepend("\\n\\n");
                String s = document.html().replaceAll("\\\\n", "\n");

                return Jsoup.clean(s, "", Safelist.none(), NO_PRETTY_PRINTING);

            }

            return null;

        } catch (Exception ex) {
            return "No story";
        }
    }

    // Collects data for the chapter list
    // We collect: chapter name + it's url
    @Override
    public ArrayList<ValuesForChapters> GetChapters(String url, Context context, HashMap<String, Object> extraData) {
        try {
            ArrayList<ValuesForChapters> data = new ArrayList<>();
            ArrayList<String> links = new ArrayList<>();
            ArrayList<String> names = new ArrayList<>();

            Elements ul = null;
            if (url.toLowerCase().contains("readmanganato.com")) {
                ul = doc.getElementsByClass("row-content-chapter");
            } else if (url.toLowerCase().contains("mangakakalot.com")) {
                ul = doc.getElementsByClass("chapter-list");
            }

            if (ul == null) {
                return null;
            }

            for (Element i : ul.first().children()) {
                String title = "";
                String link = "";

                Elements li;
                if (url.toLowerCase().contains("readmanganato.com")) {
                    li = i.getElementsByClass("chapter-name text-nowrap");
                    title = li.attr("title"); // This is the chapter title
                    link = li.attr("href"); // This is the url
                }

                else if (url.toLowerCase().contains("mangakakalot.com")) {
                    li = i.getElementsByClass("row");
                    title = li.text();
                    Elements p = li.select("a");
                    link = p.attr("href");
                }

                // We now have to edit the string so only the chapter shows
                StringBuilder sb = new StringBuilder();
                int index = 0;
                String oldTitle = title;
                boolean get = false;

                // This wants to make me kms
                int amount = 0;
                for (String p : title.split("\\s+")) {
                    if (p.equalsIgnoreCase("chapter")) {
                        amount++;
                    }
                }
                if (amount <= 1) {
                    get = true;
                }

                for (String x : title.split("\\s+")) {
                    if (x.equalsIgnoreCase("chapter")) {
                        if (get) {
                            try {
                                // We remove some clutter form the title
                                title = title.replace(":", ""); // This will fix things like "chapter 6:" 6: != double  but 6 is
                                // We want to check whether x in chapter x is a number or not
                                // We can do this by converting it to a double and trying to make it shot out an error
                                Double.parseDouble(title.split("\\s+")[index + 1]); // this is here to shoot out an error

                                sb.append(x);
                                sb.append(" ");
                                sb.append(title.split("\\s+")[index + 1]);
                                title = sb.toString();
                            } catch (Exception ex) {
                                title = oldTitle;
                            }

                            // These two lines may break shit.
                            if (title.toLowerCase().contains("chapter")) {
                                title = title.replace(':', ' ');
                            }
                            break;
                        } else {
                            get = true;
                        }
                    }
                    index++;
                }
                // Adds our data to the arraylist
                names.add(title);
                links.add(link);
            }

            // Now that we have the names and links
            // We need reverse it and add the outputs to a linkedhashmap
            Collections.reverse(names);
            Collections.reverse(links);

            for (int i = 0; i < names.size(); i++) {
                ValuesForChapters valuesForChapters = new ValuesForChapters();
                valuesForChapters.name = names.get(i);
                valuesForChapters.url = links.get(i);
                data.add(valuesForChapters);
            }

            return data;
        } catch (Exception error) {
            return new ArrayList<ValuesForChapters>();
        }

    }

    // THis part is in charge of getting the manga images
    // Mangakakalot has two servers
    @Override
    public ArrayList<String> GetImages(ValuesForChapters object, Context context) {
        Document doc;

        String url = object.url;
        try {
            Settings settings = new Settings();
            Boolean server_two = settings.ReturnValueBoolean(context, "preference_ServerMangakakalot", false);

            if (!server_two) {
                doc = Jsoup.connect(url)
                        .userAgent("Mozilla/5.0 (X11; Linux x86_64; rv:89.0) Gecko/20100101 Firefox/89.0")
                        .get();
            } else {
                String CookieSiteLocation;

                // To get to server two, we need certain cookies
                // There are two cookie sites based on whether the url is mangakakalot or readmanganato
                if (url.toLowerCase().contains("readmanganato")) {
                    CookieSiteLocation = "https://readmanganato.com/content_server_s2";
                } else {
                    CookieSiteLocation = "https://mangakakalot.com/change_content_s2";
                }

                // This'll get us the neccessary cookies.
                Connection.Response res = Jsoup.connect(CookieSiteLocation)
                        .userAgent("Mozilla/5.0 (X11; Linux x86_64; rv:89.0) Gecko/20100101 Firefox/89.0")
                        .method(Connection.Method.GET)
                        .execute();

                Map<String, String> coookies = res.cookies(); // The cookies (what a surprise)

                doc = Jsoup.connect(url)
                        .userAgent("Mozilla/5.0 (X11; Linux x86_64; rv:89.0) Gecko/20100101 Firefox/89.0")
                        .cookies(coookies)
                        .get();
            }

            // Here we do the webscraping
            ArrayList<String> images = new ArrayList<>();
            Elements ImagesHtml = doc.getElementsByClass("container-chapter-reader");
            for (Element i : ImagesHtml.first().children()) {
                Elements imgTag = i.select("img");
                String img = imgTag.attr("src");
                images.add(img);
            }
            return images;

        } catch (Exception ex) {
            return null;
        }

    }

    @Override
    public HashMap<String, String> GetRequestData(String url) {
        try {
            doc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (X11; Linux x86_64; rv:89.0) Gecko/20100101 Firefox/89.0")
                    .get();
        } catch (Exception ex) {
            return null;
        }

        HashMap<String, String> reqData = new HashMap<>();

        String referer = "";
        if (url.toLowerCase().contains("readmanganato.com")) {
            referer = "https://readmanganato.com/";
            reqData.put("Host", "s71.mkklcdnv6tempv2.com");

        } else if (url.toLowerCase().contains("mangakakalot.com")) {
            referer = "https://mangakakalot.com/";
            reqData.put("Host", "avt.mkklcdnv6temp.com");
        }
        reqData.put("Referer", referer);
        reqData.put("User-Agent", "Mozilla/5.0 (X11; Linux x86_64; rv:91.0) Gecko/20100101 Firefox/91.0");

        return reqData;
    }

    @Override
    public void PrepareReadChapter(ReadActivity readActivity) {
        Settings settings = new Settings();
        if (!settings.ReturnValueBoolean(readActivity.getApplicationContext(), "preference_mangakakalot_showButon",
                false)) {
            Log.d("lol", "NOPE");
            return;
        }

        // We inflate the button
        LayoutInflater inflater = (LayoutInflater) readActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View vieww = inflater.inflate(R.layout.switch_server, null);
        ConstraintLayout constraintLayout = readActivity.findViewById(R.id.layout_readactivity);
        constraintLayout.addView(vieww);

        // We do the epic code here for our not so epic switch
        SwitchCompat toggle = readActivity.findViewById(R.id.switchServer);
        toggle.setVisibility(View.VISIBLE);

        Boolean server = settings.ReturnValueBoolean(readActivity.getApplicationContext(),
                "preference_ServerMangakakalot", false);
        toggle.setChecked(server); // true or false
        toggle.setOnClickListener(v -> {
            if (toggle.isChecked()) {
                settings.AssignValueBoolean(readActivity.getApplicationContext(), "preference_ServerMangakakalot",
                        true);
            } else {
                settings.AssignValueBoolean(readActivity.getApplicationContext(), "preference_ServerMangakakalot",
                        false);
            }

            // readActivity.read.LoadChapter(ReadValueHolder.currentChapter);
            readActivity.read.ChangeChapter(0);

        });
    }

    @Override
    public HashMap<String, ArrayList<HomeMangaClass>> GetDataHomeActivity(Context context) {
        String URL;
        // This has been changed to mangakakalot.com/bbl, but I don't know if they'll ever turn it back to mangakakalot.com so I added a check
        URL = "https://mangakakalot.com/";
        HashMap<String, ArrayList<HomeMangaClass>> data = new HashMap<>();
        Document doc;
        try {
            doc = Jsoup.connect(URL)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:95.0) Gecko/20100101 Firefox/95.0")
                    .get();

            // This checks whether it's mangakakalot.com/bbl or mangakakalot.com
            // I don't know when or if the devs will change it back to mangakakalot.com so I added a check for it
            if (doc.getElementsByClass("bt-official-gotohome").text().trim().equalsIgnoreCase(">> VIEW FULL SITE <<")) {
                URL = "https://mangakakalot.com/kkl";
                doc = Jsoup.connect(URL)
                        .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:95.0) Gecko/20100101 Firefox/95.0")
                        .get();
            }

            // This gets the latest manga
            ArrayList<HomeMangaClass> latest = new ArrayList<>();
            Elements elements = doc.getElementsByClass("doreamon");
            for (Element i : elements) {
                Elements x = i.getElementsByClass("itemupdate first");
                for (Element y : x) {
                    Elements tooltip = y.getElementsByClass("tooltip");
                    Elements tooltip_cover = y.getElementsByClass("tooltip cover");
                    Elements img = tooltip_cover.select("img");

                    String name = tooltip.text(); // THE NAME OF THE MANGA
                    String url = tooltip.attr("href"); // The url to the manga
                    String image = img.attr("src"); // The url to the image
                    HomeMangaClass homeMangaClass = new HomeMangaClass(name, url, image, null);
                    latest.add(homeMangaClass);
                }
            }

            data.put("latest", latest);

            // This gets the most popular manga
            ArrayList<HomeMangaClass> popular = new ArrayList<>();
            Elements owl_wrapper = doc.getElementsByClass("owl-carousel");
            for (Element i : owl_wrapper) {
                String name = "";
                String image = "";
                String url = "";

                Elements owl_item = i.getElementsByClass("item");
                for (Element x : owl_item) {
                    Elements item = x.getElementsByClass("item");
                    Elements img = item.select("img");
                    image = img.attr("src"); // The image

                    // Do I really have to fucking iterate like this?
                    for (Element p : item) {
                        Elements slide_caption = p.getElementsByClass("slide-caption");
                        name = slide_caption.text().split("Chapter")[0]; // The title; we use .split to get rid of the chapter in the title

                        Elements a = p.select("a");
                        url = a.attr("href");
                    }
                    HomeMangaClass homeMangaClass = new HomeMangaClass(name, url, image, null);
                    popular.add(homeMangaClass);
                }
            }
            data.put("popular", popular);

        }

        catch (Exception exception) {
            Log.d("lol", exception.toString());
            return null;
        }

        return data;
    }

    @Override
    public String GetSourceName() {
        return "mangakakalot";
    }

}
