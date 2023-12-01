package com.example.mangareader.Recyclerviews.chapterlist;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.mangareader.R;

import java.util.List;


public class RviewAdapterChapterlist extends RecyclerView.Adapter<RviewAdapterChapterlist.ViewHolder> {

    private final LayoutInflater mInflater;
    private final HeaderInfo headerInfo;
    private final List<ChapterInfo> items;
    private final Context context;

    public RviewAdapterChapterlist(Context context, HeaderInfo headerInfo, List<ChapterInfo> items) {
        this.mInflater = LayoutInflater.from(context);

        this.headerInfo = headerInfo;
        this.items = items;
        this.context = context;
    }

    @Override
    public int getItemViewType(int position) {
        return position == 0
                ? ChapterListHeader.TYPE
                : ChapterListButton.TYPE;
    }

    // inflates the row layout from xml when needed
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case ChapterListHeader.TYPE:
                return new ChapterListHeader(mInflater, parent, R.layout.chapter_list_header);
            case ChapterListButton.TYPE:
                return new ChapterListButton(mInflater, parent, R.layout.chapter_list_button);
            default:
                throw new IllegalArgumentException("viewType does not match any known types");
        }

    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        if (position == 0) {
            ((ChapterListHeader) holder).bind(headerInfo);
            return;
        }

        ((ChapterListButton) holder).bind(items.get(position - 1));

    }

    @Override
    public int getItemCount() {
        return items.size() + 1;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(LayoutInflater inflater, @NonNull ViewGroup parent, int layoutResource) {
            super(inflater.inflate(layoutResource, parent, false));
        }
    }

}
