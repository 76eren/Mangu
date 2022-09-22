package com.example.mangareader.Recyclerviews.chapterlist;

import com.example.mangareader.SourceHandlers.Sources;

public class ChapterInfo {
    private final Sources.ValuesForChapters valuesForChapters;

    public ChapterInfo(Sources.ValuesForChapters valuesForChapters) {
        this.valuesForChapters = valuesForChapters;
    }

    public Sources.ValuesForChapters getValuesForChapters() {
        return valuesForChapters;
    }
}
