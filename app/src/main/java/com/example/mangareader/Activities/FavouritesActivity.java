package com.example.mangareader.Activities;

import android.content.pm.ActivityInfo;
import android.view.Menu;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;
import com.example.mangareader.Favourites.FavouriteItem;
import com.example.mangareader.Favourites.Favourites;
import com.example.mangareader.R;
import com.example.mangareader.Recyclerviews.RviewAdapterFavourites;
import com.example.mangareader.Settings;
import com.example.mangareader.ValueHolders.ObjectHolder;
import com.example.mangareader.navigation.Navigation;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class FavouritesActivity extends AppCompatActivity {

    public DrawerLayout drawerLayout;
    public ActionBarDrawerToggle actionBarDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourites);

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        overridePendingTransition(0,0);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);


        // drawer layout instance to toggle the menu icon to open
        // drawer and back button to close drawer
        drawerLayout = findViewById(R.id.my_drawer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close);

        // pass the Open and Close toggle for the drawer layout listener
        // to toggle the button
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        // to make the Navigation drawer icon always appear on the action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        Navigation navigation = new Navigation();
        NavigationView navigationView = findViewById(R.id.navMenu);
        Menu menu = navigationView.getMenu();
        navigation.ItemClickSetup(this, menu);


        List<RviewAdapterFavourites.Data> data = new ArrayList<>();
        LinkedHashSet<FavouriteItem> set = Favourites.GetFavourites(this);



        // Here we attempt give the manga a missing name tag that will let the user know that a name is missing.
        // This should only happen if the user converts from an old build to a newer one (a build without the feature to a build with the feature)
        for (FavouriteItem i : set) {
            if (i.mangaName == null || i.mangaName.equals("")) {
                i.mangaName = "[missing name]";
            }
        }

        Settings settings = new Settings();
        for (FavouriteItem i : set) {

            if (!settings.ReturnValueBoolean(this, "preference_merge_manga_favourites", true)) {   // Checks whether we want to merge all sources or not
                // Does not merge all sources together
                if (i.source.equals(ObjectHolder.sources.getClass().getName())) {
                    data.add(new RviewAdapterFavourites.Data(this, i));
                }
            }
            else {
                // Merges all sources together
                data.add(new RviewAdapterFavourites.Data(this, i));
            }


        }



        RecyclerView recyclerView = findViewById(R.id.rview);
        RviewAdapterFavourites adapter = new RviewAdapterFavourites(this, data, "imageview");
        recyclerView.setAdapter(adapter);




    }

    // override the onOptionsItemSelected()
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
