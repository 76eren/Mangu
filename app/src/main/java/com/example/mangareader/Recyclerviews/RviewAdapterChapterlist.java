package com.example.mangareader.Recyclerviews;

import android.app.Activity;
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
import com.example.mangareader.ValueHolders.DesignValueHolder;
import com.example.mangareader.ValueHolders.SourceObjectHolder;
import com.example.mangareader.ValueHolders.ReadValueHolder;

import java.util.ArrayList;
import java.util.List;

import static java.time.Instant.now;

public class RviewAdapterChapterlist extends RecyclerView.Adapter<RviewAdapterChapterlist.ViewHolder> {

    private final List<Data> mData;
    private final LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private final Context ctx;

    public void LoadReadActivity(Context context, String extraData) {
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
        // What the fuck have I done????

        Data data = mData.get(position);
        // ----------------------------------------------------------------------------------------------------------------------
        // Images
        holder.star.setVisibility(View.GONE);
        holder.myImageView.setVisibility(View.GONE);

        if (data.data.extraData.equals("poster")) {
            holder.myImageView.setVisibility(View.VISIBLE);
            Glide.with(this.ctx).load(data.data.imageUrl).into(holder.myImageView);
        }

        if (data.data.image.equals("the_fucking_star")) {
            holder.star.setVisibility(View.VISIBLE);

            holder.star.setOnClickListener(v -> {

                String url = data.data.mangaUrl;
                String img = data.data.imageUrl;

                // adds or removes from favourites
                Activity activity = (Activity) data.data.context;
                FavouriteItem favouriteItem = new FavouriteItem(
                        SourceObjectHolder.getSources(activity).getClass().getName(), url, img, data.data.name,
                        (int) now().getEpochSecond());
                Favourites.checkWhatNeedsToHappen(data.data.context, favouriteItem);
            });
        }

        // ---------------------------------------------------------------------------------------------------------------------------------------------------
        // Textview
        holder.myTextView.setText(data.data.tv);

        if (!data.data.tv.equals("")) {
            holder.myTextView.setVisibility(View.VISIBLE);
        } else {
            holder.myTextView.setVisibility(View.GONE);
        }

        // -----------------------------------
        // BUTTONS || pain (lots)
        if (data.data.btn != null) {
            holder.myButton.setVisibility(View.VISIBLE);
            holder.myButton.setText(data.data.btn.name);

            if (!data.data.extraData.equals("")) { // Ehh I am not happy with checking for an empty string tbh

                // I am not very happy with the way we keep track of our history
                // I'd rather have the ListTracker class keep a bunch of objects rather than
                // Strings
                ArrayList<String> history = ListTracker.GetFromList(data.data.context, "History");
                for (String i : history) {
                    if (i.equals(data.data.btn.url)) {
                        holder.myButton.setTextColor(DesignValueHolder.ButtonTextColorRead);
                        break;
                    } else {
                        // We need this so it wont break.
                        holder.myButton.setTextColor(DesignValueHolder.ButtonTextColorNotRead);
                    }
                }

                // Button click
                holder.myButton.setOnClickListener(v -> {
                    ReadValueHolder.currentChapter = data.data.btn;

                    LoadReadActivity(RviewAdapterChapterlist.this.ctx, data.data.extraData);

                });

                // Button long hold
                // This is kinda broken atm
                holder.myButton.setOnLongClickListener(v -> {
                    ListTracker.ChangeStatus(data.data.context, data.data.btn.url, "History");

                    ArrayList<String> updatedHistory = ListTracker.GetFromList(data.data.context, "History");
                    for (String i : updatedHistory) {
                        if (i.equals(data.data.btn.url)) {
                            holder.myButton.setTextColor(DesignValueHolder.ButtonTextColorRead);
                            break;
                        } else {
                            holder.myButton.setTextColor(DesignValueHolder.ButtonTextColorNotRead);
                        }
                    }

                    return true;
                });

            }
        } else {
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
        ImageView star;
        TextView myTextView;
        Button myButton;

        ViewHolder(View itemView) {
            super(itemView);
            this.myImageView = itemView.findViewById(R.id.imageViewPoster);
            this.myTextView = itemView.findViewById(R.id.tv);
            this.myButton = itemView.findViewById(R.id.btn);
            this.star = itemView.findViewById(R.id.imageViewStar);

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

        RviewChapterlistDataClass data;

        public Data(RviewChapterlistDataClass data) {
            this.data = data;
        }

    }

}
