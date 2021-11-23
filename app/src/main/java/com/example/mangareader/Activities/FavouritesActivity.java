package com.example.mangareader.Activities;

import android.view.Menu;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.mangareader.Favourites.FavouriteItem;
import com.example.mangareader.Favourites.Favourites;
import com.example.mangareader.R;
import com.example.mangareader.Recyclerviews.RviewAdapterChapterlist;
import com.example.mangareader.Recyclerviews.RviewAdapterFavourites;
import com.example.mangareader.ValueHolders.ObjectHolder;
import com.example.mangareader.navigation.Navigation;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class FavouritesActivity extends AppCompatActivity {

    public DrawerLayout drawerLayout;
    public ActionBarDrawerToggle actionBarDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourites);

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

        List<RviewAdapterFavourites.Data> data = new ArrayList<>();
        Set<FavouriteItem> set = Favourites.GetFavourites(this);
        for (FavouriteItem i : set) {
            if (i.source.equals(ObjectHolder.sources.getClass().getName())) {
                data.add(new RviewAdapterFavourites.Data(i.image, i.url, this));
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
