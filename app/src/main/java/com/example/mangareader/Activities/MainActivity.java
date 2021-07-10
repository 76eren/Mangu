package com.example.mangareader.Activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.example.mangareader.SourceHandlers.MainActivitySourceHandler;
import com.example.mangareader.PictureScreen;
import com.example.mangareader.R;
import com.example.mangareader.Settings;


public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Define our widgets
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

        // We create an empty pictureScreen object
        PictureScreen pictureScreen = new PictureScreen();
        pictureScreen.AssignContext(this);

        // First we check which source we want to get our shit from
        SharedPreferences sharedpreferences = getSharedPreferences("settings", Context.MODE_PRIVATE);
        String source = sharedpreferences.getString("source", "None");
        Settings settings = new Settings();
        settings.AssignContext(this);
        if (source.equals("None")){
            settings.Source("mangakakalot");
            source="mangakakalot";
        }

        String finalSource = source;
        search.setOnClickListener(v -> {
            // We want to stop everything if the search bar is empty
            if (input.getText().toString().equals("")) {
                return;
            }



        Thread t = new Thread(() -> {
            // The String finalsource will tell the function which function to call next
            MainActivitySourceHandler mainActivitySourceHandler = new MainActivitySourceHandler();
            mainActivitySourceHandler.Source(finalSource,previous,current,next,name,input,this);

        });
        t.start();



        });





    }
}