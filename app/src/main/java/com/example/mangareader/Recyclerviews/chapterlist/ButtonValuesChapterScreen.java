package com.example.mangareader.Recyclerviews.chapterlist;

import android.widget.Button;
import com.example.mangareader.SourceHandlers.Sources;

import java.util.HashMap;

public class ButtonValuesChapterScreen {
    private final Button selectedButton;
    private final String selectedButtonUrl;
    private final Sources.ValuesForChapters valuesForChapters;
    private final HashMap<String, Object> extraData;

    public ButtonValuesChapterScreen(Button selectedButton, String selectedButtonUrl, Sources.ValuesForChapters valuesForChapters, HashMap<String, Object> extraData) {
        this.selectedButton = selectedButton;
        this.selectedButtonUrl = selectedButtonUrl;
        this.valuesForChapters = valuesForChapters;
        this.extraData = extraData;
    }

    public Button getSelectedButton() {
        return selectedButton;
    }

    public String getSelectedButtonUrl() {
        return selectedButtonUrl;
    }

    public Sources.ValuesForChapters getValuesForChapters() {
        return valuesForChapters;
    }

    public HashMap<String, Object> getExtraData() {
        return extraData;
    }
}
