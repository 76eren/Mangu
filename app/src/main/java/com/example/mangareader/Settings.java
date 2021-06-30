package com.example.mangareader;


import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;

public class Settings {
    private Context context;

    public void Source(String source) {
        SharedPreferences sharedpreferences = this.context.getSharedPreferences("settings", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();

        switch (source){
            case "mangakakalot":
                editor.putString("source", source);
                editor.commit();
                break;
        }

    }

    public void AssignContext(Context ctx) {
        this.context = ctx;
    }




}
