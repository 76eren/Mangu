package com.example.mangareader;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.example.mangareader.mangakakalot.Search;


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
                Log.d("lol", "Empty text not falling for it");
                return;
            }

            switch (finalSource) {
                case "mangakakalot":

                    Thread t = new Thread(() -> {
                        Search search1 = new Search();
                        search1.GetLinks(input.getText().toString()); // Gets all the information we need

                        // In case our shit is empty.
                        // This is untested btw so good luck lmao
                        // this is probably unnecesary but whatever
                        if (search1.images.isEmpty()) {
                            Log.d("lol", "No results");
                            return;
                        }

                        // Now we want to update our sussy baka screen thingy
                        pictureScreen.setup(previous,current,next,name,"mangakakalot_chapterList", search1.images, search1.names, search1.urls);
                        pictureScreen.menu();

                    });
                    t.start();

                    break;
            }

        });





    }
}