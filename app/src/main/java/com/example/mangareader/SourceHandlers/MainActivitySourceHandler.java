package com.example.mangareader.SourceHandlers;

import android.app.Activity;
import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.mangareader.PictureScreen;
import com.example.mangareader.mangakakalot.Mangakakalot;

public class MainActivitySourceHandler {
    public void Source(String source, ImageView previous, ImageView current, ImageView next, TextView name, TextView input, Context context) {
        switch (source) {
            case "mangakakalot":
                Mangakakalot(previous, current, next, name, input,context);
                break;
        }
    }

    private void Mangakakalot(ImageView previous, ImageView current, ImageView next, TextView name, TextView input, Context context) {
        Mangakakalot mangakakalot = new Mangakakalot();
        mangakakalot.GetLinks(input.getText().toString()); // Gets all the information we need

        if (mangakakalot.images.isEmpty()) {
            return;
        }

        // Now we want to update our sussy baka screen thingy
        PictureScreen pictureScreen = new PictureScreen();
        pictureScreen.AssignContext(context);
        pictureScreen.setup(previous,current,next,name,"mangakakalot_chapterList", mangakakalot.images, mangakakalot.names, mangakakalot.urls);
        pictureScreen.menu();
    }

}
