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
import com.example.mangareader.Activities.ChaptersActivity;
import com.example.mangareader.Home.HomeMangaClass;
import com.example.mangareader.R;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class RviewAdapterHome extends RecyclerView.Adapter<RviewAdapterHome.ViewHolder> {

    private final List<RviewAdapterHome.Data> mData;
    private final LayoutInflater mInflater;
    private RviewAdapterFavourites.ItemClickListener mClickListener;


    // data is passed into the constructor
    public RviewAdapterHome(Context context, List<RviewAdapterHome.Data> mData, String type) {
        this.mData = mData;
        this.mInflater = LayoutInflater.from(context);
    }


    // inflates the row layout from xml when needed
    @NonNull
    @Override
    public RviewAdapterHome.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.recyclerview_home_widgets, parent, false);

        return new RviewAdapterHome.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RviewAdapterHome.Data data = mData.get(position);
        Glide.with(data.context).load(data.homeMangaClassObject.image).into(holder.imageView);
        holder.textView.setText(data.homeMangaClassObject.name);

        holder.imageView.setOnClickListener(view -> {
            Intent intent = new Intent(data.context, ChaptersActivity.class);
            intent.putExtra("url", data.homeMangaClassObject.chapterUrl);
            intent.putExtra("mangaName", data.homeMangaClassObject.name);
            intent.putExtra("img", data.homeMangaClassObject.image);
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
        ImageView imageView;
        TextView textView;

        ViewHolder(View itemView) {
            super(itemView);
            this.imageView = itemView.findViewById(R.id.home_image);
            this.textView = itemView.findViewById(R.id.home_text);

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
        HomeMangaClass homeMangaClassObject;



        public Data(Context context, HomeMangaClass homeMangaClassObject) {
            this.context = context;
            this.homeMangaClassObject = homeMangaClassObject;

        }

    }

}
