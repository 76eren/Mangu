package com.example.mangareader.Recyclerviews.chapterlist;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toolbar;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import com.example.mangareader.Activities.ReadActivity;
import com.example.mangareader.R;
import com.example.mangareader.Settings.ListTracker;
import com.example.mangareader.SourceHandlers.Sources;
import com.example.mangareader.ValueHolders.DesignValueHolder;
import com.example.mangareader.ValueHolders.ReadValueHolder;
import java.util.ArrayList;

public class ChapterListButton extends RviewAdapterChapterlist.ViewHolder {

    public static final int TYPE = 1;
    private final Button button;
    public static boolean staticShouldEnableToolbar = false;

    // ------------------------------------------------------------------------------------------
    // These 2 are tied to each other. If you touch one of them you have to touch the other one as well
    public ArrayList<Button> enabledButtons = new ArrayList<>();
    public ArrayList<Sources.ValuesForChapters> valuesForChaptersList = new ArrayList<>();
    // ------------------------------------------------------------------------------------------

    public ChapterListButton(LayoutInflater inflater, @NonNull ViewGroup parent, int layoutResource) {
        super(inflater, parent, layoutResource);
        this.button = this.itemView.findViewById(R.id.button);
    }

    public void bind (ChapterInfo chapterInfo) {
        button.setText(chapterInfo.getValuesForChapters().name);
        button.setTextColor(getButtonColor(button, chapterInfo.getValuesForChapters().url));


        button.setOnLongClickListener(v -> {
            if (!staticShouldEnableToolbar) {
                staticShouldEnableToolbar = true;
                this.setButtonColor(button, DesignValueHolder.buttonTextColorBlue);

                // For the first elements we need to add them to the list manually
                this.enabledButtons.add(button);
                this.valuesForChaptersList.add(chapterInfo.getValuesForChapters());
            }

            return true;
        });

        button.setOnClickListener(v -> {
            if (staticShouldEnableToolbar) {
                if (enabledButtons.contains(button)) {
                    enabledButtons.remove(button);
                    valuesForChaptersList.remove(chapterInfo.getValuesForChapters());
                    this.setButtonColor(button, getButtonColor(button, chapterInfo.getValuesForChapters().url));
                }
                else {
                    enabledButtons.add(button);
                    valuesForChaptersList.add(chapterInfo.getValuesForChapters());
                    this.setButtonColor(button, DesignValueHolder.buttonTextColorBlue);
                }
            }
            else {
                ReadValueHolder.currentChapter = chapterInfo.getValuesForChapters();
                Intent readIntent = new Intent(button.getContext(), ReadActivity.class);
                readIntent.putExtra("downloaded", false);
                button.getContext().startActivity(readIntent);
            }
        });
    }

    private static boolean inHistory (Context context, String url) {
        return ListTracker.getFromList(context, "History").stream()
                .anyMatch(s -> s.equals(url));
    }
    public static int getButtonColor (Button button, String url) {
        return inHistory(button.getContext(), url)
                ? DesignValueHolder.buttonTextColorRead
                : DesignValueHolder.buttonTextColorNotRead;
    }

    private void setButtonColor(Button button, int color) {
        button.setTextColor(color);
    }

}