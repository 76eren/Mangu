package com.example.mangareader.Activities;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toolbar;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;
import com.example.mangareader.Home.HomeMangaClass;
import com.example.mangareader.R;
import com.example.mangareader.Recyclerviews.RviewAdapterHome;
import com.example.mangareader.Settings;
import com.example.mangareader.SourceHandlers.Sources;
import com.example.mangareader.Sources.Mangadex;
import com.example.mangareader.Sources.Mangakakalot;
import com.example.mangareader.ValueHolders.ObjectHolder;
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

        overridePendingTransition(0,0); // fuck that animation
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // drawer layout instance to toggle the menu icon to open
        // drawer and back button to close drawer

        drawerLayout = findViewById(R.id.my_drawer_layout);

        actionBarDrawerToggle = new ActionBarDrawerToggle(this,
                drawerLayout,
                R.string.nav_open,
                R.string.nav_close);


        actionBarDrawerToggle.syncState();



        // pass the Open and Close toggle for the drawer layout listener
        // to toggle the button
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        // to make the Navigation drawer icon always appear on the action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }        actionBarDrawerToggle.syncState();



        Navigation navigation = new Navigation();
        actionBarDrawerToggle.syncState();

        NavigationView navigationView = findViewById(R.id.navMenu);
        Menu menu = navigationView.getMenu();
        navigation.ItemClickSetup(this, menu);




        // We first assign our right source
        Settings settings = new Settings();
        String src = settings.ReturnValueString(this, "source", "mangakakalot");

        switch (src) {
            case "mangakakalot":
                ObjectHolder.sources = new Mangakakalot();

                break;

            case "mangadex":
                ObjectHolder.sources = new Mangadex();
                break;


            default:
                ObjectHolder.sources = new Mangadex();
                break;
        }

        // We set the correct theme
        // This is so fucking lazy
        String theme = settings.ReturnValueString(this, "theme", "default");
        switch (theme) {
            case "default":
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                break;

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

        // Now we get all of the data for the activity
        Sources source = ObjectHolder.sources;

        Context context = this;
        new Thread(() -> {
            HashMap<String, ArrayList<HomeMangaClass>> homeData = source.GetDataHomeActivity();
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
                    Log.d("lol", "yes");
                    List<RviewAdapterHome.Data> dataPopular = new ArrayList<>();
                    for (HomeMangaClass i : popular) {
                        dataPopular.add(new RviewAdapterHome.Data(context, i));
                    }

                    runOnUiThread(() -> {
                        RecyclerView recyclerView = findViewById(R.id.RecyclerViewHomePopular);
                        RviewAdapterHome adapter = new RviewAdapterHome(context, dataPopular, "imageview");
                        recyclerView.setAdapter(adapter);

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