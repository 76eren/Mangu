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
import com.example.mangareader.R;
import com.example.mangareader.SourceHandlers.Sources;

import java.util.List;

public class RviewAdapterSearch extends RecyclerView.Adapter<RviewAdapterSearch.ViewHolder> {

    private final List<Data> mData;
    private final LayoutInflater mInflater;
    private RviewAdapterFavourites.ItemClickListener mClickListener;

    // data is passed into the constructor
    public RviewAdapterSearch(Context context, List<RviewAdapterSearch.Data> data, String type) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;

    }

    // inflates the row layout from xml when needed
    @NonNull
    @Override
    public RviewAdapterSearch.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.recyclerview_search_widgets, parent, false);

        return new RviewAdapterSearch.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RviewAdapterSearch.ViewHolder holder, int position) {
        RviewAdapterSearch.Data data = mData.get(position);

        holder.textView.setVisibility(View.VISIBLE);
        holder.image.setVisibility(View.VISIBLE);
        holder.cardView.setVisibility(View.VISIBLE);

        if (data.searchValues.referer != null) {
            GlideUrl url = new GlideUrl(data.searchValues.image, new LazyHeaders.Builder()
                    .addHeader("Referer", data.searchValues.referer)
                    .build());

            Glide.with(data.context).load(url).into(holder.image);
        }

        else {
            Glide.with(data.context).load(data.searchValues.image).into(holder.image);
        }


        holder.textView.setText(data.searchValues.name);

        holder.cardView.setOnClickListener(view -> {
            Intent intent = new Intent(data.context, ChaptersActivity.class);
            intent.putExtra("url", data.searchValues.url);
            Log.d("lol", data.searchValues.url);
            intent.putExtra("downloaded", false);
            intent.putExtra("img", data.searchValues.image);
            intent.putExtra("mangaName", data.searchValues.name);
            intent.putExtra("referer", data.searchValues.referer); // This may be null

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
        ImageView image;
        TextView textView;
        CardView cardView;

        ViewHolder(View itemView) {
            super(itemView);
            this.image = itemView.findViewById(R.id.card_favs_image_downloads);
            this.textView = itemView.findViewById(R.id.card_favs_text_downloads);
            this.cardView = itemView.findViewById(R.id.card_favs_downloads);

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
        Sources.SearchValues searchValues;

        public Data(Context context, Sources.SearchValues searchValues) {
            this.context = context;
            this.searchValues = searchValues;

        }

    }

}
