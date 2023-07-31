package com.example.mangareader;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.preference.PreferenceManager;

public class Settings {

    public String returnValueString(Context context, String value, String defaultVal) {
        SharedPreferences sharedpreferences = PreferenceManager.getDefaultSharedPreferences(context);

        return sharedpreferences.getString(value, defaultVal);

    }

    public void assignValueString(Context context, String key, String value) {
        SharedPreferences sharedpreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(key, value);
        editor.apply();

    }

    public Boolean returnValueBoolean(Context context, String value, boolean defaultVal) {
        SharedPreferences sharedpreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedpreferences.getBoolean(value, defaultVal);

    }

    public void assignValueBoolean(Context context, String key, Boolean value) {

        SharedPreferences sharedpreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putBoolean(key, value);
        editor.apply();

    }

}
