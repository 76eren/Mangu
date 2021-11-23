package com.example.mangareader.Activities;

import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.drawerlayout.widget.DrawerLayout;
import com.example.mangareader.PictureScreen;
import com.example.mangareader.R;
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


public class MainActivity extends AppCompatActivity {

    public DrawerLayout drawerLayout;
    public ActionBarDrawerToggle actionBarDrawerToggle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


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

        overridePendingTransition(0,0);


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

        // Define our widgetse
        Button search =  findViewById(R.id.search);

        TextView input = findViewById(R.id.input);
        TextView name = findViewById(R.id.name);
        ImageView previous = findViewById(R.id.prev);
        ImageView current = findViewById(R.id.curr);
        ImageView next = findViewById(R.id.next);

        // We make the imageviews invisible
        previous.setVisibility(View.INVISIBLE);
        current.setVisibility(View.INVISIBLE);
        next.setVisibility(View.INVISIBLE);


        search.setOnClickListener(v -> {
            // We want to stop everything if the search bar is empty
            if (input.getText().toString().equals("")) {
                return;
            }

        Thread t = new Thread(() -> {

            Sources sources = ObjectHolder.sources;

            ArrayList<Sources.ValuesForCollectDataPicScreen> pictureScreenData = sources.CollectDataPicScreen(input.getText().toString());

            if (pictureScreenData == null) {
                Log.d("lol", "its null");
            }

            if (pictureScreenData != null && pictureScreenData.size() != 0) {
                Log.d("lol", String.valueOf(pictureScreenData.size()));
                // Now we want to update our sussy baka screen thingy
                // We create an empty pictureScreen object
                PictureScreen pictureScreen = new PictureScreen();
                pictureScreen.AssignContext(this);

                pictureScreen.setup(previous,current,next,name, pictureScreenData);
                pictureScreen.menu();
            }
            else {
                runOnUiThread(() -> Toast.makeText(getApplicationContext(),"Couldn't find the manga you're looking for",Toast.LENGTH_SHORT).show());
            }




        });
        t.start();

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