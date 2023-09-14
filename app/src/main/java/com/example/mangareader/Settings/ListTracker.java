// NOTE TO FUTURE SELF
// I'm not happy with the state of this class as of right now
// I kinda want to change it so it behaves like The Favourites class
// Right now we don't have much "mobility", like we can only store Strings and not objects
// For now it's not a problem but if I ever want to add a new feature that requites this class it might be a problem.

package com.example.mangareader.Settings;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.preference.PreferenceManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class ListTracker {
    public static void addToList(Context context, String data, String setting) {
        if (data != null) {
            SharedPreferences sharedpreferences = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor editor = sharedpreferences.edit();

            Set<String> set = new HashSet<>(sharedpreferences.getStringSet(setting, Collections.emptySet()));
            set.add(data);

            editor.putStringSet(setting, set);
            editor.apply();
        }

    }

    public static void changeStatus(Context context, String data, String setting) {
        SharedPreferences sharedpreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedpreferences.edit();

        Set<String> set = new HashSet<>(sharedpreferences.getStringSet(setting, Collections.emptySet()));
        boolean got = false;
        ArrayList<String> x = new ArrayList<>(set);

        for (String i : x) {
            if (i.equals(data)) {
                set.remove(data);
                got = true;
            }
        }
        if (!got) {
            set.add(data);
        }

        editor.putStringSet(setting, set);
        editor.apply();

    }

    public static ArrayList<String> getFromList(Context context, String setting) {
        SharedPreferences sharedpreferences = PreferenceManager.getDefaultSharedPreferences(context);
        Set<String> set = sharedpreferences.getStringSet(setting, Collections.emptySet());

        return new ArrayList<>(set);

    }

}
