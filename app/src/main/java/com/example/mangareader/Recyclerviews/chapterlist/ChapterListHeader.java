package com.example.mangareader.Recyclerviews.chapterlist;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.example.mangareader.Favourites.FavouriteItem;
import com.example.mangareader.Favourites.Favourites;
import com.example.mangareader.R;
import com.example.mangareader.ValueHolders.SourceObjectHolder;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;

public class ChapterListHeader extends RviewAdapterChapterlist.ViewHolder {

    public static final int TYPE = 0;
    private final ImageView poster;
    private final TextView description;

    public ChapterListHeader(LayoutInflater inflater, @NonNull @NotNull ViewGroup parent, int layoutResource) {
        super(inflater, parent, layoutResource);
        this.poster = this.itemView.findViewById(R.id.poster);
        this.description = this.itemView.findViewById(R.id.description_text);
    }


    public void bind(HeaderInfo data) {
        // Currently uses internet and does not take into account the downloading
        if (data.getReferer() == null) {
            Glide.with(poster.getContext())
                    .load(data.getMangaImageUrl())
                    .placeholder(R.drawable.ic_launcher_background)
                    .into(poster);
        } else {
            GlideUrl url = new GlideUrl(data.getMangaImageUrl(), new LazyHeaders.Builder()
                    .addHeader("Referer", data.getReferer())
                    .build());

            Glide.with(poster.getContext())
                    .load(url)
                    .placeholder(R.drawable.ic_launcher_background)
                    .into(poster);
        }

        description.setText(data.getDescription());

    }


}
