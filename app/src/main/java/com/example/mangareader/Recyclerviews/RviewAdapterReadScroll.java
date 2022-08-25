package com.example.mangareader.Recyclerviews;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.mangareader.R;
import com.example.mangareader.Read.Read;
import com.example.mangareader.Read.ReadScroll;
import com.github.chrisbanes.photoview.PhotoView;

import java.util.HashMap;
import java.util.List;

public class RviewAdapterReadScroll extends RecyclerView.Adapter<RviewAdapterReadScroll.ViewHolder> {
    private final List<Data> mData;
    private final LayoutInflater mInflater;
    private RviewAdapterFavourites.ItemClickListener mClickListener;

    // data is passed into the constructor
    public RviewAdapterReadScroll(Context context, List<RviewAdapterReadScroll.Data> mData, String type) {
        this.mData = mData;
        this.mInflater = LayoutInflater.from(context);
    }

    // inflates the row layout from xml when needed
    @Override
    @NonNull
    public RviewAdapterReadScroll.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.recyclerview_widgets_read, parent, false);

        return new RviewAdapterReadScroll.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RviewAdapterReadScroll.Data data = mData.get(position);
        holder.button.setVisibility(View.GONE);
        holder.photoView.setVisibility(View.GONE);

        if (data.button.equals("")) {
            holder.photoView.setVisibility(View.VISIBLE);
            Read.LoadImage(data.url, holder.photoView, data.reqData, data.context);

        } else {
            holder.button.setVisibility(View.VISIBLE);
            holder.button.setText(data.button);
            holder.button.setOnClickListener(view -> {
                if (data.button.equals("Next chapter")) {
                    data.readScroll.ChangeChapter(1);
                }

                else if (data.button.equals("Previous chapter")) {
                    data.readScroll.ChangeChapter(-1);
                }
            });

        }

    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        PhotoView photoView;
        Button button;

        ViewHolder(View itemView) {
            super(itemView);
            this.photoView = itemView.findViewById(R.id.read_photoview);
            this.button = itemView.findViewById(R.id.button_scroll);

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
        String url;
        HashMap<String, String> reqData;
        String button;
        ReadScroll readScroll;

        public Data(Context context, String url, HashMap<String, String> reqData, String buton, ReadScroll readScroll) {
            this.context = context;
            this.url = url;
            this.reqData = reqData;
            this.button = buton;
            this.readScroll = readScroll;
        }

    }

}
