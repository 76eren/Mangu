package com.example.mangareader;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.preference.PreferenceManager;

public class Settings {

    public String ReturnValueString(Context context, String value, String defaultVal) {
        SharedPreferences sharedpreferences = PreferenceManager.getDefaultSharedPreferences(context);

        return sharedpreferences.getString(value, defaultVal);

    }

    public void AssignValueString(Context context, String key, String value) {
        SharedPreferences sharedpreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(key, value);
        editor.apply();

    }

    public Boolean ReturnValueBoolean(Context context, String value, boolean defaultVal) {
        SharedPreferences sharedpreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedpreferences.getBoolean(value, defaultVal);

    }

    public void AssignValueBoolean(Context context, String key, Boolean value) {

        SharedPreferences sharedpreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putBoolean(key, value);
        editor.apply();

    }

}
