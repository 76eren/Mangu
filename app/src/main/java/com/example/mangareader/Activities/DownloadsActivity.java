package com.example.mangareader.Activities;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;
import com.example.mangareader.Downloading.DownloadTracker;
import com.example.mangareader.Downloading.DownloadedChapter;
import com.example.mangareader.R;
import com.example.mangareader.Recyclerviews.RviewAdapterDownloads;
import com.example.mangareader.Settings.Settings;
import com.example.mangareader.ValueHolders.SourceObjectHolder;
import com.example.mangareader.navigation.Navigation;
import com.google.android.material.navigation.NavigationView;

import java.util.*;


public class DownloadsActivity extends AppCompatActivity {
    public DrawerLayout drawerLayout;
    public ActionBarDrawerToggle actionBarDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_downloads);

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        overridePendingTransition(0, 0);

        drawerLayout = findViewById(R.id.my_drawer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close);

        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        Navigation navigation = new Navigation();
        NavigationView navigationView = findViewById(R.id.navMenu);
        Menu menu = navigationView.getMenu();
        navigation.itemClickSetup(this, menu);

        List<RviewAdapterDownloads.Data> data = new ArrayList<>();
        DownloadTracker downloadTracker = new DownloadTracker();
        LinkedHashSet<DownloadedChapter> set = downloadTracker.getFromDownloads(this);


        Settings settings = new Settings();

        // Sorts our downloads
        ArrayList<DownloadedChapter> temp = new ArrayList<>(set);
        ArrayList<DownloadedChapter> sortedDownloads = new ArrayList<>();
        for (DownloadedChapter i : temp) {
            boolean canAdd = true;
            for (DownloadedChapter y : sortedDownloads) {
                if (y.getName().equals(i.getName())) {
                    canAdd = false;
                    break;
                }
            }
            if (canAdd) {
                sortedDownloads.add(i);
            }
        }


        switch (settings.returnValueString(this, "preference_favourites_sort", "preference_favourites_sort_date_up")) {
            case "preference_favourites_sort_date_down":
                sortedDownloads.sort(Comparator.comparingInt(DownloadedChapter::getDate));
                break;

            case "preference_favourites_sort_date_up":
                sortedDownloads.sort(Comparator.comparingInt(DownloadedChapter::getDate));
                Collections.reverse(sortedDownloads);
                break;

            case "preference_favourites_sort_alphabet":
                sortedDownloads.sort(Comparator.comparing(DownloadedChapter::getName));
                break;
        }

        for (DownloadedChapter i : sortedDownloads) {
            if (!settings.returnValueBoolean(this, "preference_merge_manga_favourites", true)) {
                // Checks whether we want to merge all sources or not
                // Does not merge all sources together
                if (i.getSource().equals(SourceObjectHolder.getSources(this).getClass().getName())) {
                    data.add(new RviewAdapterDownloads.Data(this, i));
                }
            } else {
                // Merges all sources together
                data.add(new RviewAdapterDownloads.Data(this, i));
            }
        }

        RecyclerView recyclerView = findViewById(R.id.rviewDownloads);
        RviewAdapterDownloads adapter = new RviewAdapterDownloads(this, data, "imageview");
        recyclerView.setAdapter(adapter);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}