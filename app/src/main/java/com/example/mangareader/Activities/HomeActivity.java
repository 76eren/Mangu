package com.example.mangareader.Activities;

import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;
import com.example.mangareader.Home.HomeMangaClass;
import com.example.mangareader.R;
import com.example.mangareader.Recyclerviews.RviewAdapterHome;
import com.example.mangareader.Settings.Settings;
import com.example.mangareader.SourceHandlers.Sources;
import com.example.mangareader.ValueHolders.SourceObjectHolder;
import com.example.mangareader.navigation.Navigation;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    public DrawerLayout drawerLayout;
    public ActionBarDrawerToggle actionBarDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        overridePendingTransition(0, 0);

        getSupportActionBar().setDisplayShowTitleEnabled(false);

        drawerLayout = findViewById(R.id.my_drawer_layout);

        actionBarDrawerToggle = new ActionBarDrawerToggle(this,
                drawerLayout,
                R.string.nav_open,
                R.string.nav_close);

        actionBarDrawerToggle.syncState();

        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        actionBarDrawerToggle.syncState();

        Navigation navigation = new Navigation();
        actionBarDrawerToggle.syncState();

        NavigationView navigationView = findViewById(R.id.navMenu);
        Menu menu = navigationView.getMenu();
        navigation.itemClickSetup(this, menu);

        Settings settings = new Settings();
        TextView waitTV = findViewById(R.id.waitTV);
        TextView PopularManga = findViewById(R.id.PopularManga);
        TextView LatestManga = findViewById(R.id.LatestManga);

        PopularManga.setVisibility(View.INVISIBLE);
        LatestManga.setVisibility(View.INVISIBLE);

        // We set the correct theme
        // This is very lazy
        String theme = settings.returnValueString(this.getApplicationContext(), "theme", "default");
        switch (theme) {

            case "dark":
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                break;

            case "light":
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;

            default:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                break;
        }

        // Now we get all the data for the activity
        Sources source = SourceObjectHolder.getSources(this); // This both sets and gets the source

        // This shouldn't be here.
        if (source.getClass().getName().equals("com.example.mangareader.Sources.Mangadex")) {
            waitTV.setText("Loading the home screen. This may take a while the first time");
        }

        Context context = this;
        new Thread(() -> {
            HashMap<String, ArrayList<HomeMangaClass>> homeData = null;

            try {
                homeData = source.getDataHomeActivity(this);
            } catch (InterruptedException e) {
                homeData = null;
            }

            if (homeData != null) {
                ArrayList<HomeMangaClass> latest = homeData.get("latest");

                // Does the latest
                if (latest != null) {
                    // We first start with the latest
                    List<RviewAdapterHome.Data> data = new ArrayList<>();
                    for (HomeMangaClass i : latest) {
                        data.add(new RviewAdapterHome.Data(context, i));
                    }

                    runOnUiThread(() -> {
                        RecyclerView recyclerView = findViewById(R.id.RecyclerViewHomeLatest);

                        RviewAdapterHome adapter = new RviewAdapterHome(context, data, "imageview");
                        recyclerView.setAdapter(adapter);

                    });
                }

                // Does the popular
                ArrayList<HomeMangaClass> popular = homeData.get("popular");

                if (popular != null) {
                    List<RviewAdapterHome.Data> dataPopular = new ArrayList<>();
                    for (HomeMangaClass i : popular) {
                        dataPopular.add(new RviewAdapterHome.Data(context, i));
                    }

                    runOnUiThread(() -> {
                        RecyclerView recyclerView = findViewById(R.id.RecyclerViewHomePopular);
                        RviewAdapterHome adapter = new RviewAdapterHome(context, dataPopular, "imageview");
                        recyclerView.setAdapter(adapter);

                        PopularManga.setVisibility(View.VISIBLE);
                        LatestManga.setVisibility(View.VISIBLE);
                        waitTV.setVisibility(View.GONE);

                    });
                }

            }

        }).start();

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}