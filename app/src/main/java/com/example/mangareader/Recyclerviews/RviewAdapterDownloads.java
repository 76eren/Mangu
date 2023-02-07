package com.example.mangareader.Recyclerviews;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.example.mangareader.Activities.ChaptersActivity;
import com.example.mangareader.Downloading.DownloadedChapter;
import com.example.mangareader.R;
import com.example.mangareader.SourceHandlers.Sources;
import com.example.mangareader.ValueHolders.SourceObjectHolder;

import java.util.List;

public class RviewAdapterDownloads extends RecyclerView.Adapter<RviewAdapterDownloads.ViewHolder> {

    private final List<Data> mData;
    private final LayoutInflater mInflater;
    private RviewAdapterFavourites.ItemClickListener mClickListener;

    // data is passed into the constructor
    public RviewAdapterDownloads(Context context, List<RviewAdapterDownloads.Data> data, String type) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;

    }

    // inflates the row layout from xml when needed
    @NonNull
    @Override
    public RviewAdapterDownloads.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.recyclerview_widgets_favs, parent, false);

        return new RviewAdapterDownloads.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RviewAdapterDownloads.ViewHolder holder, int position) {
        RviewAdapterDownloads.Data data = mData.get(position);

        // Currently this still uses internet.
        if (data.downloadedChapter.getReferer() == null) {
            Glide.with(data.context)
                    .load(data.downloadedChapter.getImage())
                    .placeholder(R.drawable.ic_launcher_background)
                    .into(holder.cardImage);
        } else {
            GlideUrl url = new GlideUrl(data.downloadedChapter.getImage(), new LazyHeaders.Builder()
                    .addHeader("Referer", data.downloadedChapter.getReferer())
                    .build());

            Glide.with(data.context)
                    .load(url)
                    .placeholder(R.drawable.ic_launcher_background)
                    .into(holder.cardImage);
        }

        holder.cardText.setText(data.downloadedChapter.getMangaName());

        holder.card.setOnClickListener(v -> {
            Intent intent = new Intent(data.context, ChaptersActivity.class);
            intent.putExtra("downloaded", true);
            intent.putExtra("url", data.downloadedChapter.getUrl());
            intent.putExtra("img", data.downloadedChapter.getImage());
            intent.putExtra("mangaName", data.downloadedChapter.getMangaName());
            intent.putExtra("referer", data.downloadedChapter.getReferer());

            // Sets the correct source
            // This is unnecessary if we have to merge manga option turned off by the way
            try {
                Class<?> c = Class.forName(data.downloadedChapter.getSource());
                Object obj = c.newInstance();

                // Right now this just straight up changes the source :/
                SourceObjectHolder.ChangeSource((Sources) obj, data.context);


            } catch (Exception ex) {
                // I dont't think our program is going to like kill itself if this here errors out
                // This shouldn't throw an error though
                Log.d("lol", ex.toString());
            }

            data.context.startActivity(intent);

        });

    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        CardView card;
        TextView cardText;
        ImageView cardImage;

        ViewHolder(View itemView) {
            super(itemView);

            card = itemView.findViewById(R.id.card_favs_downloads);
            cardText = itemView.findViewById(R.id.card_favs_text_downloads);
            cardImage = itemView.findViewById(R.id.card_favs_image_downloads);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null)
                mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }

    public static class Data {
        Context context;
        DownloadedChapter downloadedChapter;

        public Data(Context context, DownloadedChapter downloadedChapter) {
            this.context = context;
            this.downloadedChapter = downloadedChapter;
        }

    }

}
