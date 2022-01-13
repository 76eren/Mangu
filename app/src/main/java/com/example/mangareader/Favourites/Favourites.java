package com.example.mangareader.Favourites;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.preference.PreferenceManager;
import com.google.gson.*;
import java.util.*;

public class Favourites {
    public static void AddToFavourites(Context context, FavouriteItem favouriteItem) {
        if (favouriteItem.url != null) {
            SharedPreferences sharedpreferences = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor editor = sharedpreferences.edit();

            // We create a gson object
            Gson gson = new Gson();

            // we get our current favourite items
            Set<String> favs = new LinkedHashSet<>(sharedpreferences.getStringSet("Favourites", new LinkedHashSet<>()));

            // We create a FavouriteItem object and convert it to a json
            String json = gson.toJson(favouriteItem);
            // We add our json to the Set with favourites
            favs.add(json);

            // We push our set to the sharedprefs
            editor.putStringSet("Favourites", favs);
            editor.apply();
        }

    }

    public static void RemoveFromFavourites(Context context, FavouriteItem favouriteItem) {
        // THIS FUN IS BROKEN LOL
        SharedPreferences sharedpreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedpreferences.edit();

        Gson gson = new Gson();

        // Our set containing strings
        Set<String> favsSetStrings = new LinkedHashSet<>(sharedpreferences.getStringSet("Favourites", new LinkedHashSet<>()));

        // We remove the object from the Set
        favsSetStrings.remove(gson.toJson(favouriteItem));

        // We push it to the sharedpreferences
        editor.putStringSet("Favourites", favsSetStrings);
        editor.apply();


    }

    public static void checkWhatNeedsToHappen(Context context, FavouriteItem favouriteItem) {
        SharedPreferences sharedpreferences = PreferenceManager.getDefaultSharedPreferences(context);

        Gson gson = new Gson();

        // Our set containing strings
        Set<String> favsSetStrings = new LinkedHashSet<>(sharedpreferences.getStringSet("Favourites", new LinkedHashSet<>()));

        // We create an identical object bla bla bla

        if (favsSetStrings.contains(gson.toJson(favouriteItem))) {
            RemoveFromFavourites(context, favouriteItem);
        }
        else {
            AddToFavourites(context, favouriteItem);
        }


    }

    public static Set<FavouriteItem> GetFavourites (Context context) {
        SharedPreferences sharedpreferences = PreferenceManager.getDefaultSharedPreferences(context);

        // This is our hashset with Strings
        Set<String> favsSetStrings = new LinkedHashSet<>(sharedpreferences.getStringSet("Favourites", new LinkedHashSet<>()));

        // Now we need to convert it to a hashset with Objects
        Set<FavouriteItem> setObjects = new LinkedHashSet<>();
        Gson gson = new Gson();
        for (String i : favsSetStrings) {
            FavouriteItem favouriteItem = gson.fromJson(i, FavouriteItem.class);
            setObjects.add(favouriteItem);
        }

        return setObjects;


    }
}


