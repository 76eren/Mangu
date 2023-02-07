package com.example.mangareader.Recyclerviews.chapterlist;

import android.widget.Button;
import com.example.mangareader.SourceHandlers.Sources;

import java.util.HashMap;

public class ButtonValuesChapterScreen {
    private Button selectedButton;
    private String selectedButtonUrl;
    private Sources.ValuesForChapters valuesForChapters;
    private HashMap<String, Object> extraData;

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
