package com.example.mangareader.Favourites;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import androidx.preference.PreferenceManager;
import com.example.mangareader.SourceHandlers.Sources;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.*;

public class Favourites {
    public static void AddToFavourites(Context context, String source, String url, String image) {
        Log.d("lol", "Adding");
        if (url != null) {
            SharedPreferences sharedpreferences = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor editor = sharedpreferences.edit();

            // We create a gson object
            Gson gson = new Gson();

            // we get our current favourite items
            Set<String> favs = new HashSet<String>(sharedpreferences.getStringSet("Favourites", new HashSet<>()));

            // We create a FavouriteItem object and convert it to a json
            FavouriteItem favouriteItem = new FavouriteItem(source, url, image);
            String json = gson.toJson(favouriteItem);
            // We add our json to the Set with favourites
            favs.add(json);

            // We push our set to the sharedprefs
            editor.putStringSet("Favourites", favs);
            editor.apply();
        }

    }

    public static void RemoveFromFavourites(Context context, String source, String url, String image) {
        // THIS FUN IS BROKEN LOL
        Log.d("lol", "Removing");
        SharedPreferences sharedpreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedpreferences.edit();

        Gson gson = new Gson();

        // Our set containing strings
        Set<String> favsSetStrings = new HashSet<>(sharedpreferences.getStringSet("Favourites", new HashSet<>()));

        // We create an identical object
        FavouriteItem favouriteItem = new FavouriteItem(source,url, image);

        // We remove the object from the Set
        favsSetStrings.remove(gson.toJson(favouriteItem));

        // We push it to the sharedpreferences
        editor.putStringSet("Favourites", favsSetStrings);
        editor.apply();


    }

    public static void checkWhatNeedsToHappen(Context context, String source, String url, String image) {
        SharedPreferences sharedpreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedpreferences.edit();

        Gson gson = new Gson();

        // Our set containing strings
        Set<String> favsSetStrings = new HashSet<String>(sharedpreferences.getStringSet("Favourites", new HashSet<>()));

        // We create an identical object bla bla bla
        FavouriteItem favouriteItem = new FavouriteItem(source, url, image);

        if (favsSetStrings.contains(gson.toJson(favouriteItem))) {
            RemoveFromFavourites(context, source, url, image);
        }
        else {
            AddToFavourites(context,source,url, image);
        }


    }

    public static Set<FavouriteItem> GetFavourites (Context context) {
        SharedPreferences sharedpreferences = PreferenceManager.getDefaultSharedPreferences(context);

        // This is our hashset with Strings
        Set<String> favsSetStrings = new HashSet<String>(sharedpreferences.getStringSet("Favourites", new HashSet<>()));

        // Now we need to convert it to a hashset with Objects
        Set<FavouriteItem> setObjects = new HashSet<>();
        Gson gson = new Gson();
        for (String i : favsSetStrings) {
            FavouriteItem favouriteItem = gson.fromJson(i, FavouriteItem.class);
            setObjects.add(favouriteItem);
        }

        return setObjects;


    }
}


