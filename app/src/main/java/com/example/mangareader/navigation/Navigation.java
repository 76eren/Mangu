package com.example.mangareader.navigation;

import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import com.example.mangareader.Activities.FavouritesActivity;
import com.example.mangareader.Activities.MainActivity;
import com.example.mangareader.Activities.SettingsActivity;
import com.example.mangareader.R;

public class Navigation {

    public void ItemClickSetup(Activity activity, Menu menu) {

        MenuItem settings = menu.findItem(R.id.nav_settings);

        settings.setOnMenuItemClickListener(item -> {
            // We go to a new activity of course
            Intent intent = new Intent(activity, SettingsActivity.class);

            activity.startActivity(intent);

            return false;
        });

        MenuItem favourites = menu.findItem(R.id.nav_favourites);
        favourites.setOnMenuItemClickListener(item -> {
            // We go to a new activity of course
            Intent intent = new Intent(activity, FavouritesActivity.class);
            activity.startActivity(intent);

            return false;
        });

        MenuItem home = menu.findItem(R.id.nav_home);
        home.setOnMenuItemClickListener(item -> {
            // We go to a new activity of course
            Intent intent = new Intent(activity, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            activity.startActivity(intent);

            return false;
        });


    }





}
