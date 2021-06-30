package com.example.mangareader.mangakakalot;

import android.util.Log;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

public class GetImages {

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

}
