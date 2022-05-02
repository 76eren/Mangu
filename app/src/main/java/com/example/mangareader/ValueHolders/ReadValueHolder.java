package com.example.mangareader.ValueHolders;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import com.example.mangareader.Activities.HomeActivity;
import com.example.mangareader.SourceHandlers.Sources;

import java.util.ArrayList;
import java.util.Currency;
import java.util.LinkedHashMap;

public class ReadValueHolder {


    public static ArrayList<Sources.ValuesForChapters> ChaptersActivityData;

    public static ArrayList<Sources.ValuesForChapters> getChaptersActivityData(Activity activity) {
        if (ChaptersActivityData != null) {
            return ChaptersActivityData;

        }
        else {
            // I don't think this is like ever going to run, but we might as well put this here you never know.
            Intent intent = new Intent(activity, HomeActivity.class);
            activity.startActivity(intent);

            return new ArrayList<Sources.ValuesForChapters>();
        }
    }


    public static Sources.ValuesForChapters currentChapter;

    public static Sources.ValuesForChapters getCurrentChapter(Activity activity) {
        if (currentChapter != null) {
            return currentChapter;
        }

        else {
            // I don't think this is like ever going to run, but we might as well put this here you never know.
            Intent intent = new Intent(activity, HomeActivity.class);
            activity.startActivity(intent);
            return  new Sources.ValuesForChapters();
        }
    }



}
