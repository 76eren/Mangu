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
import com.example.mangareader.Read.Readmodes;
import com.github.chrisbanes.photoview.PhotoView;

import java.util.HashMap;
import java.util.List;

public class RviewAdapterReadScroll extends RecyclerView.Adapter<RviewAdapterReadScroll.ViewHolder> {
    private final List<Data> mData;
    private final LayoutInflater mInflater;
    private final List<DataDownload> mDataDownload;
    private RviewAdapterFavourites.ItemClickListener mClickListener;

    // data is passed into the constructor
    public RviewAdapterReadScroll(Context context, List<RviewAdapterReadScroll.Data> mData, List<DataDownload> mDataDownload) {
        this.mData = mData;
        this.mInflater = LayoutInflater.from(context);
        this.mDataDownload = mDataDownload;
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
        Data data = null;
        if (mData != null) {
            data = mData.get(position);
        }

        DataDownload dataDownload = null;
        if (mDataDownload != null) {
            dataDownload = mDataDownload.get(position);
        }

        holder.button.setVisibility(View.GONE);
        holder.photoView.setVisibility(View.GONE);

        // This means we didn't download anything
        if (data != null) {
            if (data.button.equals("")) {
                holder.photoView.setVisibility(View.VISIBLE);
                Read.LoadImage(data.url, holder.photoView, data.reqData, data.context);
            } else {
                holder.button.setVisibility(View.VISIBLE);
                holder.button.setText(data.button);
                Data finalData = data;
                holder.button.setOnClickListener(view -> {
                    if (finalData.button.equals("Next chapter")) {
                        finalData.readScroll.ChangeChapter(1);
                    } else if (finalData.button.equals("Previous chapter")) {
                        finalData.readScroll.ChangeChapter(-1);
                    }
                });

            }
        }

        // This means we did download the images
        else if (dataDownload != null) {
            if (!dataDownload.button.equals("")) {
                // The code for the next and previous button
                holder.button.setVisibility(View.VISIBLE);
                holder.button.setText(dataDownload.button);
                DataDownload finalDataDownload = dataDownload;
                holder.button.setOnClickListener(v -> {
                    if (finalDataDownload.button.equals("Next chapter")) {
                        finalDataDownload.readScroll.changeChapterDownloads(1);
                    } else if (finalDataDownload.button.equals("Previous chapter")) {
                        finalDataDownload.readScroll.changeChapterDownloads(-1);
                    }
                });

            } else {
                // The code for the actual image loading
                holder.photoView.setVisibility(View.VISIBLE);
                Read.LoadImageDownload(dataDownload.context, holder.photoView, dataDownload.imageName, dataDownload.path);

            }


        }

    }

    // total number of rows
    @Override
    public int getItemCount() {
        if (mData != null) {
            return mData.size();
        } else {
            return mDataDownload.size();
        }
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
        Readmodes.DownloadData downloadData;

        public Data(Context context, String url, HashMap<String, String> reqData, String buton, ReadScroll readScroll, Readmodes.DownloadData downloadData) {
            this.context = context;
            this.url = url;
            this.reqData = reqData;
            this.button = buton;
            this.readScroll = readScroll;
            this.downloadData = downloadData;
        }

    }

    public static class DataDownload {
        Context context;
        String button;
        String imageName;
        String path;
        ReadScroll readScroll;
        Readmodes.DownloadData downloadData;

        public DataDownload(Context context, String image, String path, String button, ReadScroll readScroll, Readmodes.DownloadData downloadData) {
            this.context = context;
            this.imageName = image;
            this.path = path;
            this.button = button;
            this.readScroll = readScroll;
            this.downloadData = downloadData;
        }
    }

}
