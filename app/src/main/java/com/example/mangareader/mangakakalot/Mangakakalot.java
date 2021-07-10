package com.example.mangareader.mangakakalot;

import android.util.Log;
import org.apache.commons.lang3.StringEscapeUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Whitelist;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;

public class Mangakakalot {
    private Document doc;

    public String getStory(String url) {

        try {
            doc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (X11; Linux x86_64; rv:89.0) Gecko/20100101 Firefox/89.0")
                    .get();
        }
        catch (Exception ex) {
            return "";
        }

        // FUCK MANGAKAKLOT FOR PULLING THIS SHIT ON ME HONESTLY
        // MANGAKAKLOT USES MULTIPLE WEBSITES


        Elements x = null;
        if (url.toLowerCase().contains("readmanganato.com")) {
            x = doc.getElementsByClass("panel-story-info-description"); // This also contains data we don't need
        }
        else if (url.toLowerCase().contains("mangakakalot.com")) {
            x = doc.select("div#noidungm");
        }

        Document document = Jsoup.parse(x.html());
        document.outputSettings(new Document.OutputSettings().prettyPrint(false)); //makes html() preserve linebreaks and spacing
        document.select("br").append("\\n");
        document.select("p").prepend("\\n\\n");
        String s = document.html().replaceAll("\\\\n", "\n");
        String storyUnreadable =  Jsoup.clean(s, "", Whitelist.none(), new Document.OutputSettings().prettyPrint(false));
        String story = StringEscapeUtils.unescapeHtml3(storyUnreadable); // using deprecated libraries is fun

        return story;

    }

    public LinkedHashMap<String,String> GetChapters(String url) {
        try {
            // Here we get all of our urls and shit
            // We can just re-use the document we created when getting the story


            LinkedHashMap<String,String> NameUrl = new LinkedHashMap<>(); // name:url
            ArrayList<String> links = new ArrayList<>();
            ArrayList<String> names = new ArrayList<>();

            Log.d("lol", "Scraping referer urls");

            Elements ul = null;
            if (url.toLowerCase().contains("readmanganato.com")) {
                ul = doc.getElementsByClass("row-content-chapter");
            }
            else if (url.toLowerCase().contains("mangakakalot.com")) {
                ul = doc.getElementsByClass("chapter-list");
            }

            for (Element i : ul.first().children()) {
                String title = "";
                String link = "";

                Elements li = null;
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
                int index=0;
                boolean get=false;
                for (String x : title.split("\\s+")) {
                    if (x.equalsIgnoreCase("chapter")) {
                        if (get) {
                            sb.append(x);
                            sb.append(" ");
                            sb.append(title.split("\\s+")[index+1]);
                            title=sb.toString();

                            // These two lines may break shit.
                            if (title.toLowerCase().contains("chapter")) {
                                title = title.replace(':', ' ');
                            }
                            break;
                        }
                        else {
                            get=true;
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

            for (int i = 0; i < names.size();i++) {
                NameUrl.put(names.get(i), links.get(i));
            }

            return NameUrl;

        }
        catch (Exception ex) {
            Log.d("lol", ex.toString());
            return null;
        }
    }


    public ArrayList<String> GetImages(String url) {
        Document doc;
        try {
            doc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (X11; Linux x86_64; rv:89.0) Gecko/20100101 Firefox/89.0")
                    .get();
        }
        catch (Exception ex) {
            return null;
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
    }


    public ArrayList<String> images = new ArrayList<>();
    public ArrayList<String> urls = new ArrayList<>();
    public ArrayList<String> names = new ArrayList<>();

    public void GetLinks(String manga) {
        manga = manga.replace(" ", "_");
        String url = "https://mangakakalot.com/search/story/"+manga;
        Document doc;
        try {
            doc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (X11; Linux x86_64; rv:89.0) Gecko/20100101 Firefox/89.0")
                    .get();
        }
        catch (Exception ex) {
            doc = null;
        }

        if (doc == null) {
            return;
        }


        Elements elems = doc.getElementsByClass("panel_story_list");
        for (Element i : elems.first().children()) {
            Elements x = i.getElementsByClass("story_item");
            Elements y = x.select("a");
            Elements z = y.select("img");

            String img = z.attr("src"); // A link to the sussy baka image
            String link = y.attr("href"); // A link to the sussy baka chapter list
            String name = z.attr("alt"); // The manga's sussy baka name

            // Now we need to add our stuff to the arrays
            images.add(img);
            urls.add(link);
            names.add(name);

        }
    }

}
