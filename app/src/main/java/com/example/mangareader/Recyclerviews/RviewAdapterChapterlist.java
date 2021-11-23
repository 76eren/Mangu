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
import com.example.mangareader.Activities.ReadActivity;
import com.example.mangareader.Favourites.FavouriteItem;
import com.example.mangareader.Favourites.Favourites;
import com.example.mangareader.R;
import com.example.mangareader.ListTracker;
import com.example.mangareader.SourceHandlers.Sources;
import com.example.mangareader.ValueHolders.DesignValueHolder;
import com.example.mangareader.ValueHolders.ObjectHolder;
import com.example.mangareader.ValueHolders.ReadValueHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class RviewAdapterChapterlist extends RecyclerView.Adapter<RviewAdapterChapterlist.ViewHolder> {

    private final List<Data> mData;
    private final LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private final Context ctx;

    public void LoadReadActivity(Context context, String extraData) {
        Log.d("lol", extraData);
        Intent readIntent = new Intent(context, ReadActivity.class);
        context.startActivity(readIntent);
    }


    // data is passed into the constructor
    public RviewAdapterChapterlist(Context context, List<Data> data, String type) {
        this.mInflater = LayoutInflater.from(context);

        this.mData = data;
        this.ctx = context;

    }

    // inflates the row layout from xml when needed
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.recyclerview_widgets, parent, false);

        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        Data data = mData.get(position);

        // ----------------------------------------------------------------------------------------------------------------------
        // Imageview


        // I am not happy with checking this way but whatever
        if (data.extraData.equals("poster")) {

            // I am pretty sure hardcoding this is going to cause issues in the future....
            holder.myImageView.getLayoutParams().width = 973;
            holder.myImageView.getLayoutParams().height = 528;

            Glide.with(this.ctx).load(data.image).into(holder.myImageView);
        }

        if (data.image.equals("the_fucking_star")) {

            holder.myImageView.setImageResource(R.drawable.star_colored);
            holder.myImageView.getLayoutParams().height = 64;
            holder.myImageView.getLayoutParams().width = 64;

            holder.myImageView.setOnClickListener(v -> {
                String x = data.extraData;
                String url = x.split("_")[0];
                String img = x.split("_")[1];
                Favourites.checkWhatNeedsToHappen(data.context, ObjectHolder.sources.getClass().getName(), url, img);
                Set<FavouriteItem> newHistory = Favourites.GetFavourites(data.context);
                FavouriteItem favouriteItem = new FavouriteItem(ObjectHolder.sources.getClass().getName(), url, img);
                boolean found = false;
                for (FavouriteItem i : newHistory) {
                    if (Objects.equals(i.source, favouriteItem.source) && Objects.equals(i.url, favouriteItem.url) && Objects.equals(i.image, favouriteItem.image)) {
                        found = true;
                        break;
                    }
                }

                if (found) {
                    Toast.makeText(data.context,"Added manga to favourites",Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(data.context,"Removed manga from favourites",Toast.LENGTH_SHORT).show();
                }

            });
        }

        if (!data.image.equals("")) {
            holder.myImageView.setVisibility(View.VISIBLE);
        }
        else {
            holder.myImageView.setVisibility(View.GONE);
        }

        // ---------------------------------------------------------------------------------------------------------------------------------------------------
        // Textview

        holder.myTextView.setText(data.tv);
        if (!data.extraData.equals("")) {
            holder.myTextView.setOnClickListener(v -> {
                // Changes our story layout
                LinearLayout.LayoutParams storyParamsWrap = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );
                storyParamsWrap.setMargins(5,10,5,10);
                holder.myTextView.setLayoutParams(storyParamsWrap);
            });
        }

        if (!data.tv.equals("")) {
            holder.myTextView.setVisibility(View.VISIBLE);
        }
        else {
            holder.myTextView.setVisibility(View.GONE);
        }



        // -----------------------------------
        // BUTTONS || pain (lots)
        if (data.btn != null) {
            holder.myButton.setVisibility(View.VISIBLE);
            holder.myButton.setText(data.btn.name);


            if (!data.extraData.equals("")) { // Ehh I am not happy with checking for an empty string tbh

                // I am not very happy with the way we keep track of our history
                // I'd rather have the ListTracker class keep a bunch of objects rather than  Strings
                ArrayList<String> history = ListTracker.GetFromList(data.context, "History");
                for (String i : history) {
                    if (i.equals(data.extraData)) {
                        holder.myButton.setTextColor(DesignValueHolder.ButtonTextColorRead);
                        break;
                    }
                    else {
                        // We need this so it wont break.
                        holder.myButton.setTextColor(DesignValueHolder.ButtonTextColorNotRead);
                    }
                }

                // Button click
                holder.myButton.setOnClickListener(v -> {
                    ReadValueHolder.currentChapter = data.btn;
                    LoadReadActivity(RviewAdapterChapterlist.this.ctx, data.extraData);
                });

                //Button long hold
                // This is kinda broken atm
                holder.myButton.setOnLongClickListener(v -> {
                    ListTracker.ChangeStatus(data.context, data.extraData, "History");
                    ArrayList<String> updatedHistory = ListTracker.GetFromList(data.context, "History");
                    for (String i : updatedHistory) {
                        if (i.equals(data.extraData)) {
                            holder.myButton.setTextColor(DesignValueHolder.ButtonTextColorRead);
                            break;
                        }
                        else {
                            holder.myButton.setTextColor(DesignValueHolder.ButtonTextColorNotRead);
                        }

                    }

                    return true;
                });


            }
        }
        else {
            holder.myButton.setVisibility(View.GONE);
        }
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView myImageView;
        TextView myTextView;
        Button myButton;

        ViewHolder(View itemView) {
            super(itemView);
            this.myImageView = itemView.findViewById(R.id.imageView);
            this.myTextView = itemView.findViewById(R.id.tv);
            this.myButton = itemView.findViewById(R.id.btn);

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
        String tv;
        Sources.ValuesForChapters btn;
        String extraData;
        Context context;

        public Data(String image, String tv, Sources.ValuesForChapters btn, String extraData, Context context) {
            this.image = image;
            this.tv = tv;
            this.btn = btn;
            this.context = context;
            this.extraData = extraData;
        }

    }

}

