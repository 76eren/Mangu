package com.example.mangareader.Recyclerviews;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.mangareader.Activities.ChaptersActivity;
import com.example.mangareader.Favourites.FavouriteItem;
import com.example.mangareader.R;
import com.example.mangareader.SourceHandlers.Sources;
import com.example.mangareader.ValueHolders.ObjectHolder;

import java.util.List;

public class RviewAdapterFavourites extends RecyclerView.Adapter<RviewAdapterFavourites.ViewHolder>{

    private final List<RviewAdapterFavourites.Data> mData;
    private final LayoutInflater mInflater;
    private RviewAdapterFavourites.ItemClickListener mClickListener;


    // data is passed into the constructor
    public RviewAdapterFavourites(Context context, List<RviewAdapterFavourites.Data> data, String type) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;

    }

    // inflates the row layout from xml when needed
    @NonNull
    @Override
    public RviewAdapterFavourites.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.recyclerview_widgets_favs, parent, false);

        return new RviewAdapterFavourites.ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(RviewAdapterFavourites.ViewHolder holder, int position) {
        RviewAdapterFavourites.Data data = mData.get(position);

        Glide.with(data.context).load(data.favouriteItem.image).into(holder.cardImage);
        holder.cardText.setText(data.favouriteItem.mangaName);

        holder.card.setOnClickListener(v -> {
            Intent intent = new Intent(data.context, ChaptersActivity.class);
            intent.putExtra("url", data.favouriteItem.url);
            intent.putExtra("img", data.favouriteItem.image);
            intent.putExtra("mangaName", data.favouriteItem.mangaName);

            // Sets the correct source
            // This is unncecesary if we have to merge manga option turned off by the way
            try {
                Class c;
                c = Class.forName(data.favouriteItem.source);
                Object obj = c.newInstance();
                Sources source = Sources.class.cast(obj);
                ObjectHolder.sources = source;
            }
            catch (Exception ex) {
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

            card = itemView.findViewById(R.id.card_favs);
            cardText = itemView.findViewById(R.id.card_favs_text);
            cardImage = itemView.findViewById(R.id.card_favs_image);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }

    public static class Data {
        Context context;
        FavouriteItem favouriteItem;

        public Data(Context context, FavouriteItem favouriteItem) {
            this.context = context;
            this.favouriteItem=favouriteItem;
        }

    }


}
