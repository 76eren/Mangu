
package com.example.mangareader.Favourites;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;
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
            LinkedHashSet<String> favs = new LinkedHashSet<>(sharedpreferences.getStringSet("Favourites", new LinkedHashSet<>()));

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

        SharedPreferences sharedpreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedpreferences.edit();

        Gson gson = new Gson();

        // Our set containing strings
        LinkedHashSet<String> favsSetStrings = new LinkedHashSet<>(sharedpreferences.getStringSet("Favourites", new LinkedHashSet<>()));

        // I can't just remove the already existing object because not all values are the same
        // e.g the time value will always be different because the time we're calling this function is different from the time the manga was added
        // Instead we can just check for urls
        // Not all values are the same so we cannot just make an identical object and remove the identical object from the String array
        // it is a bit annoying but hey it is what it is....

        // Also I am dumb

        for (String i : favsSetStrings) {
            if (gson.fromJson(i, FavouriteItem.class).url.equals(favouriteItem.url)) {
                favsSetStrings.remove(i);
                break;
            }
        }

        // We push it to the sharedpreferences
        editor.putStringSet("Favourites", favsSetStrings);
        editor.apply();


    }

    public static void checkWhatNeedsToHappen(Context context, FavouriteItem favouriteItem) {
        SharedPreferences sharedpreferences = PreferenceManager.getDefaultSharedPreferences(context);

        Gson gson = new Gson();

        // Our set containing strings
        LinkedHashSet<String> favsSetStrings = new LinkedHashSet<>(sharedpreferences.getStringSet("Favourites", new LinkedHashSet<>()));

        boolean found = false;

        for (String i : favsSetStrings) {
            // We compare URLs to check what needs to happen
            FavouriteItem y = gson.fromJson(i, FavouriteItem.class);

            if (favouriteItem.url.equals(y.url)) {
                found=true;
                break;
            }
        }

        if (found) {
            RemoveFromFavourites(context, favouriteItem);
            Toast.makeText(context, "Removed manga from favourites",Toast.LENGTH_SHORT).show();

        }

        else {
            AddToFavourites(context, favouriteItem);
            Toast.makeText(context, "Added manga to favourites",Toast.LENGTH_SHORT).show();

        }


    }

    public static LinkedHashSet<FavouriteItem> GetFavourites (Context context) {
        SharedPreferences sharedpreferences = PreferenceManager.getDefaultSharedPreferences(context);

        // This is our hashset with Strings
        LinkedHashSet<String> favsSetStrings = new LinkedHashSet<>(sharedpreferences.getStringSet("Favourites", new LinkedHashSet<>()));

        // Now we need to convert it to a hashset with Objects
        LinkedHashSet<FavouriteItem> setObjects = new LinkedHashSet<>();
        Gson gson = new Gson();
        for (String i : favsSetStrings) {
            FavouriteItem favouriteItem = gson.fromJson(i, FavouriteItem.class);
            setObjects.add(favouriteItem);
        }

        return setObjects;


    }
}