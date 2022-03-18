package com.example.mangareader.Activities;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;
import com.example.mangareader.R;
import com.example.mangareader.Recyclerviews.RviewAdapterSearch;
import com.example.mangareader.Settings;
import com.example.mangareader.Sources.Mangadex;
import com.example.mangareader.Sources.Mangakakalot;
import com.example.mangareader.ValueHolders.ObjectHolder;
import com.example.mangareader.SourceHandlers.Sources;


// I DID USE CODE FROM HERE: https://www.geeksforgeeks.org/navigation-drawer-in-android/
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import com.example.mangareader.navigation.Navigation;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    public DrawerLayout drawerLayout;
    public ActionBarDrawerToggle actionBarDrawerToggle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        overridePendingTransition(0,0); // fuck that animation
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getSupportActionBar().setDisplayShowTitleEnabled(false);


        // drawer layout instance to toggle the menu icon to open
        // drawer and back button to close drawer
        drawerLayout = findViewById(R.id.my_drawer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close);


        // pass the Open and Close toggle for the drawer layout listener
        // to toggle the button
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        // to make the Navigation drawer icon always appear on the action bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Navigation navigation = new Navigation();
        NavigationView navigationView = findViewById(R.id.navMenu);
        Menu menu = navigationView.getMenu();
        navigation.ItemClickSetup(this, menu);



        TextView search = findViewById(R.id.search);
        Context context = this;

        search.setOnEditorActionListener((textView, i, keyEvent) -> {
            new Thread(() -> {
                List<RviewAdapterSearch.Data> data = new ArrayList<>();
                ArrayList<Sources.SearchValues> searchResults = ObjectHolder.sources.CollectDataPicScreen(search.getText().toString());

                if (searchResults != null) { // prevents a dirty little nullpointerexception
                    for (Sources.SearchValues i1 : searchResults) {
                        data.add(new RviewAdapterSearch.Data(context, i1));
                    }
                    runOnUiThread(() -> {
                        RecyclerView recyclerView = findViewById(R.id.RecyclerViewHomeLatest);
                        RviewAdapterSearch adapter = new RviewAdapterSearch(context, data, "imageview");
                        recyclerView.setAdapter(adapter);

                    });
                }


            }).start();


            return false;
        });
    }

    // override the onOptionsItemSelected()z
    // function to implement
    // the item click listener callback
    // to open and close the navigation
    // drawer when the icon is clicked
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


}