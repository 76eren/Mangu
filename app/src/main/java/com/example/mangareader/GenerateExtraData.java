package com.example.mangareader;

import java.util.HashMap;

public class GenerateExtraData {

    public HashMap<String, Object> generateExtraData(String mangaUrl, String imageUrl, String mangaName, String referer, String mangaStory) {
        HashMap<String, Object> extraData = new HashMap<>();

        extraData.put("mangaName", mangaName);
        extraData.put("imageUrl", imageUrl);
        extraData.put("referer", referer);
        extraData.put("mangaUrl", mangaUrl);
        extraData.put("mangaStory", mangaStory);

        return extraData;
    }

    public HashMap<String, Object> addChapterNamesDefaultOrder(HashMap<String, Object> map, String[] value) {
        map.put("chapterNamesDefaultOrder", value);
        return map;
    }
}
