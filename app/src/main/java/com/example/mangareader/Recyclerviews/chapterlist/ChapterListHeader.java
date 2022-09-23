package com.example.mangareader.Recyclerviews.chapterlist;

import android.content.Context;
import android.view.LayoutInflater;
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

    private final ImageView favouriteStar;
    private final ImageView poster;
    private final TextView description;
    public ChapterListHeader(LayoutInflater inflater, @NonNull @NotNull ViewGroup parent, int layoutResource) {
        super(inflater, parent, layoutResource);
        this.favouriteStar = this.itemView.findViewById(R.id.favourite_star);
        this.poster        = this.itemView.findViewById(R.id.poster);
        this.description   = this.itemView.findViewById(R.id.description_text);
    }

    public void bind (HeaderInfo data) {

        favouriteStar.setOnClickListener(v -> {

            String url = data.getMangaUrl();
            String img = data.getMangaImageUrl();

            // adds or removes from favourites
            Context context = favouriteStar.getContext();
            FavouriteItem favouriteItem = new FavouriteItem(
                    SourceObjectHolder.getSources(context).getClass().getName(), url, img, data.getMangaName(),
                    (int) Instant.now().getEpochSecond(), data.getReferer());
            Favourites.checkWhatNeedsToHappen(context, favouriteItem);

        });

        if (data.getReferer() == null) {
            Glide.with(poster.getContext())
                    .load(data.getMangaImageUrl())
                    .into(poster);
        }
        else {
            GlideUrl url = new GlideUrl(data.getMangaImageUrl(), new LazyHeaders.Builder()
                    .addHeader("Referer", data.getReferer())
                    .build());

            Glide.with(poster.getContext()).load(url).into(poster);
        }

        description.setText(data.getDescription());

    }

}
