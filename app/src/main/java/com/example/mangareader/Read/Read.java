// This script is just a collection of functions used by the read activity and the readmdodes
// The purpose of this script is so don't have to use the same code in 69 different classes
// but instead can just call this global class with a bunch of static methods

package com.example.mangareader.Read;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.example.mangareader.ListTracker;
import com.example.mangareader.R;
import com.example.mangareader.Settings;
import com.example.mangareader.SourceHandlers.Sources;
import com.example.mangareader.ValueHolders.ReadValueHolder;
import com.github.chrisbanes.photoview.PhotoView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Objects;

import static com.example.mangareader.ValueHolders.ObjectHolder.sources;

public class Read {

    public static void Cache(Activity activity, ArrayList<String> images, HashMap<String, String> reqData) {
        // This function should cache all of the images right before the reading starts
        // This function isn't working correctly right now
        // I am not even sure whether this is the right way to do caching or not

        TextView cacheStatusUpdate = activity.findViewById(R.id.cache);

        int index = 1;
        for (String i : images) {

            int finalIndex = index;
            activity.runOnUiThread(() -> cacheStatusUpdate.setText("Caching image " + finalIndex + " out of " + images.size()));

            GlideUrl url = new GlideUrl(i.trim(), new LazyHeaders.Builder()
                    .addHeader("Referer", Objects.requireNonNull(reqData.get("Referer")))
                    .build());

            RequestOptions options = new RequestOptions()
                    .diskCacheStrategy(DiskCacheStrategy.ALL);


            Glide.with(activity)
                    .load(url)
                    .apply(options)
                    .preload();



            index++;
        }

        activity.runOnUiThread(() -> cacheStatusUpdate.setVisibility(PhotoView.GONE));
    }

    public static void LoadImage(String url, PhotoView photoView, HashMap<String,String> reqData, Context context) {

        GlideUrl glideUrl;
        if (reqData != null) {
            // This sets the correct request data, so we can access the image.
            glideUrl = new GlideUrl(url, new LazyHeaders.Builder()
                    .addHeader("Referer", Objects.requireNonNull(reqData.get("Referer")))
                    .build());
        }
        else {
            glideUrl = new GlideUrl(url, new LazyHeaders.Builder()
                    .build());

        }

        // Sheesh
        Glide.with(context)
                .load(glideUrl)
                .timeout(0)
                .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL) // Fixes an issue with the image resizing when using it from cache
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(photoView);
    }
}
