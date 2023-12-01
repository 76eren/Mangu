package com.example.mangareader.Recyclerviews.chapterlist;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.annotation.NonNull;
import com.example.mangareader.Activities.ReadActivity;
import com.example.mangareader.R;
import com.example.mangareader.Settings.ListTracker;
import com.example.mangareader.ValueHolders.DesignValueHolder;
import com.example.mangareader.ValueHolders.ReadValueHolder;

public class ChapterListButton extends RviewAdapterChapterlist.ViewHolder {

    public static final int TYPE = 1;

    private final Button button;
    public ChapterListButton(LayoutInflater inflater, @NonNull ViewGroup parent, int layoutResource) {
        super(inflater, parent, layoutResource);
        this.button = this.itemView.findViewById(R.id.button);
    }

    public void bind (ChapterInfo chapterInfo) {
        button.setText(chapterInfo.getValuesForChapters().name);
        button.setTextColor(getButtonColor(button, chapterInfo.getValuesForChapters().url));
        button.setOnClickListener(v -> {
            ReadValueHolder.currentChapter = chapterInfo.getValuesForChapters();
            Intent readIntent = new Intent(button.getContext(), ReadActivity.class);
            button.getContext().startActivity(readIntent);
        });
        button.setOnLongClickListener(v -> {
            ListTracker.changeStatus(button.getContext(), chapterInfo.getValuesForChapters().url, "History");
            button.setTextColor(getButtonColor(button, chapterInfo.getValuesForChapters().url));
            return true;
        });
    }

    private static boolean inHistory (Context context, String url) {
        return ListTracker.getFromList(context, "History").stream()
                .anyMatch(s -> s.equals(url));
    }
    private static int getButtonColor (Button button, String url) {
        return inHistory(button.getContext(), url)
                ? DesignValueHolder.buttonTextColorRead
                : DesignValueHolder.buttonTextColorNotRead;
    }

}