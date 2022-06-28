package com.example.mangareader.Activities;

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
import com.example.mangareader.ValueHolders.SourceObjectHolder;
import com.example.mangareader.navigation.Navigation;
import com.google.android.material.navigation.NavigationView;

import java.util.*;

public class FavouritesActivity extends AppCompatActivity {

    public DrawerLayout drawerLayout;
    public ActionBarDrawerToggle actionBarDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourites);

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
        navigation.ItemClickSetup(this, menu);

        List<RviewAdapterFavourites.Data> data = new ArrayList<>();

        LinkedHashSet<FavouriteItem> set = Favourites.GetFavourites(this);

        // We sort the set with favourites however we want
        Settings settings = new Settings();
        ArrayList<FavouriteItem> sortedFavourites = new ArrayList<>(set);

        switch (settings.ReturnValueString(this, "preference_favourites_sort", "preference_favourites_sort_date_up")) {
            case "preference_favourites_sort_date_down":
                sortedFavourites.sort(Comparator.comparingInt(FavouriteItem::returnDate));
                break;

            case "preference_favourites_sort_date_up":
                sortedFavourites.sort(Comparator.comparingInt(FavouriteItem::returnDate));
                Collections.reverse(sortedFavourites);
                break;

            case "preference_favourites_sort_alphabet":
                sortedFavourites.sort(Comparator.comparing(FavouriteItem::returnName));
                break;

        }

        for (FavouriteItem i : sortedFavourites) {
            if (!settings.ReturnValueBoolean(this, "preference_merge_manga_favourites", true)) {
                // Checks whether we want to merge all sources or not
                // Does not merge all sources together
                if (i.source.equals(SourceObjectHolder.getSources(this).getClass().getName())) {
                    data.add(new RviewAdapterFavourites.Data(this, i));
                }
            } else {
                // Merges all sources together
                data.add(new RviewAdapterFavourites.Data(this, i));
            }
        }

        RecyclerView recyclerView = findViewById(R.id.rview);
        RviewAdapterFavourites adapter = new RviewAdapterFavourites(this, data, "imageview");
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
