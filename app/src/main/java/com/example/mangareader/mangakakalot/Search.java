package com.example.mangareader.mangakakalot;


import android.util.Log;
import com.example.mangareader.MainActivity;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Search {

    public ArrayList<String> images = new ArrayList<>();
    public ArrayList<String> urls = new ArrayList<>();
    public ArrayList<String> names = new ArrayList<>();


    public void GetLinks(String manga) {
        // Do regex here UwU lol xd rawr
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
