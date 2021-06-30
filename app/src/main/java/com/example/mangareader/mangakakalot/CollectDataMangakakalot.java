package com.example.mangareader.mangakakalot;

import android.util.Log;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.internal.StringUtil;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Whitelist;
import org.jsoup.select.Elements;

import java.util.*;

public class CollectDataMangakakalot {
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

        Elements x = doc.getElementsByClass("panel-story-info-description"); // This also contains data we don't need
        // https://stackoverflow.com/questions/5640334/how-do-i-preserve-line-breaks-when-using-jsoup-to-convert-html-to-plain-text
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

            Elements ul = doc.getElementsByClass("row-content-chapter");
            for (Element i : ul.first().children()) {
                Elements li = i.getElementsByClass("chapter-name text-nowrap");
                String title = li.attr("title"); // This is the chapter title
                String link = li.attr("href"); // This is the url

                // We now have to edit the string so only the chapter shows
                // Naruto chapter Vol.72 Chapter 700.1 : Book Of Thunder
                // This can probably also be done using regex...

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

}
