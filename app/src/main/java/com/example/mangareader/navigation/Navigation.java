package com.example.mangareader.navigation;

import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import com.example.mangareader.Activities.*;
import com.example.mangareader.R;

public class Navigation {
    public void ItemClickSetup(Activity activity, Menu menu) {

        MenuItem settings = menu.findItem(R.id.nav_settings);
        settings.setOnMenuItemClickListener(item -> {
            Intent intent = new Intent(activity, SettingsActivity.class);

            activity.startActivity(intent);

            return false;
        });

        MenuItem favourites = menu.findItem(R.id.nav_favourites);
        favourites.setOnMenuItemClickListener(item -> {
            Intent intent = new Intent(activity, FavouritesActivity.class);
            activity.startActivity(intent);

            return false;
        });

        MenuItem search = menu.findItem(R.id.nav_search);
        search.setOnMenuItemClickListener(item -> {
            Intent intent = new Intent(activity, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            activity.startActivity(intent);

            return false;
        });

        MenuItem home = menu.findItem(R.id.nav_home);
        home.setOnMenuItemClickListener(menuItem -> {
            Intent intent = new Intent(activity, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

            activity.startActivity(intent);
            return false;

        });

        MenuItem downloads = menu.findItem(R.id.nav_downloads);
        downloads.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                Intent intent = new Intent(activity, DownloadsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                activity.startActivity(intent);
                return false;
            }
        });

    }

}
