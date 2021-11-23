package com.example.mangareader.Recyclerviews;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.mangareader.Activities.ChaptersActivity;
import com.example.mangareader.R;

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

        Glide.with(data.context).load(data.image).into(holder.myImageView);

        holder.myImageView.setOnClickListener(v -> {
            Intent intent = new Intent(data.context, ChaptersActivity.class);
            intent.putExtra("url", data.location);
            intent.putExtra("img", data.image);
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
        ImageView myImageView;

        ViewHolder(View itemView) {
            super(itemView);
            this.myImageView = itemView.findViewById(R.id.imageViewFavs);

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
        String image;
        String location;
        Context context;

        public Data(String image, String location, Context context) {
            this.image = image;
            this.location = location;
            this.context = context;
        }

    }


}
