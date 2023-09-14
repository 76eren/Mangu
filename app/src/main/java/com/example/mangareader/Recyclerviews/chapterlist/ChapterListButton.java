// This class is actually on life support oh my god
// Like actually this is just awful my god what is this why

package com.example.mangareader.Recyclerviews.chapterlist;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.NonNull;
import com.example.mangareader.Activities.ChaptersActivity;
import com.example.mangareader.Activities.DownloadsActivity;
import com.example.mangareader.Activities.ReadActivity;
import com.example.mangareader.Downloading.DownloadTracker;
import com.example.mangareader.Downloading.Downloader;
import com.example.mangareader.Settings.ListTracker;
import com.example.mangareader.R;
import com.example.mangareader.ValueHolders.DesignValueHolder;
import com.example.mangareader.ValueHolders.ReadValueHolder;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

public class ChapterListButton extends RviewAdapterChapterlist.ViewHolder {

    public static final int TYPE = 1;
    private static final CopyOnWriteArrayList<ButtonValuesChapterScreen> values = new CopyOnWriteArrayList<>();
    public static Button staticDownloadButton;
    public static Button staticReadUnreadButton;
    public static Button staticRemoveDownloadsWithImages;
    // I am aware that using a bunch of statics goes against the rules of OOP but in this case I kind of have to
    // Because I can't just access the object of this class from outside
    private static int buttonMode = 0; // 0 = click, 1 = select
    private final Button button;

    public ChapterListButton(LayoutInflater inflater, @NonNull ViewGroup parent, int layoutResource) {
        super(inflater, parent, layoutResource);
        this.button = this.itemView.findViewById(R.id.button);
    }

    public static int getButtonMode() {
        return buttonMode;
    }

    public static void setButtonMode(int buttonMode) {
        ChapterListButton.buttonMode = buttonMode;
    }

    public static void resetButtons() {

        for (ButtonValuesChapterScreen i : values) {
            i.getSelectedButton().setTextColor(getButtonColor(i.getSelectedButton(), i.getSelectedButtonUrl(), true));
        }

        values.clear();

        staticReadUnreadButton.setVisibility(View.INVISIBLE);
        staticDownloadButton.setVisibility(View.INVISIBLE);
        staticRemoveDownloadsWithImages.setVisibility(View.INVISIBLE);
        buttonMode = 0;
    }

    private static boolean inHistory(Context context, String url) {
        return ListTracker.getFromList(context, "History").stream()
                .anyMatch(s -> s.equals(url));
    }

    private static int getButtonColor(Button button, String url, boolean isClearing) {
        if (!isClearing) {
            for (ButtonValuesChapterScreen i : values) {
                if (i.getSelectedButtonUrl().equals(url)) {
                    return DesignValueHolder.buttonTextColorBlue;
                }
            }
        }

        return inHistory(button.getContext(), url)
                ? DesignValueHolder.buttonTextColorRead
                : DesignValueHolder.buttonTextColorNotRead;
    }

    @SuppressLint("SetTextI18n")
    public void bind(ChapterInfo chapterInfo) {
        button.setText(chapterInfo.getValuesForChapters().name);
        button.setTextColor(getButtonColor(button, chapterInfo.getValuesForChapters().url, false));


        staticReadUnreadButton.setOnClickListener(view -> {
            for (ButtonValuesChapterScreen i : values) {
                ListTracker.changeStatus(i.getSelectedButton().getContext(), i.getSelectedButtonUrl(), "History");
                button.setTextColor(getButtonColor(i.getSelectedButton(), i.getSelectedButtonUrl(), false));

            }
            resetButtons();
        });

        staticRemoveDownloadsWithImages.setOnClickListener(v -> {
            Downloader downloader = new Downloader();
            downloader.remove(values);

            // We remove the actual downloads from the DownloadTracker here
            // I am also making a copy of the DownloadTracker in the RemoveService from within the downloader.remove() function
            // The reason I am doing this is that I want to immediately remove the chapter from the download tracker while
            // still being able to go through the old download tracker to get certain data for removing the images
            // For example if I removed the downloads at the very end of the RemoveService I would still be able to access the
            // Removed chapter while it's being removed which could lead into crashes or other errors
            // This method still doesn't completely eliminate the window between being able to view the button and not being able to view the image while removing
            DownloadTracker downloadTracker = new DownloadTracker();
            downloadTracker.removeFromDownloads(values, button.getContext());

            Toast.makeText(button.getContext(), "Removing selected manga from downloads", Toast.LENGTH_LONG).show();

            resetButtons();

            // We go back to the downloads activity
            // This is to prevent the user from pressing any buttons
            // For example the user shouldn't be able to press a button of a chapter that has been removed.
            // Currently, this has its flaws
            Intent downloadsActivityIntent = new Intent(button.getContext(), DownloadsActivity.class);
            downloadsActivityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            button.getContext().startActivity(downloadsActivityIntent);
        });

        staticDownloadButton.setOnClickListener(view -> {
            Activity activity = (Activity) button.getContext();
            Intent intent = activity.getIntent();
            if (intent.getBooleanExtra("downloaded", false)) {
                // Removing the downloads
                DownloadTracker downloadTracker = new DownloadTracker();
                downloadTracker.removeFromDownloads(values, button.getContext());
                Toast.makeText(button.getContext(), "Removing selected manga from downloads", Toast.LENGTH_LONG).show();

                resetButtons();

                // We go back to the downloads activity
                // This is to prevent the user from pressing any buttons
                // For example the user shouldn't be able to press a button of a chapter that has been removed.
                Intent downloadsActivityIntent = new Intent(button.getContext(), DownloadsActivity.class);
                downloadsActivityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                button.getContext().startActivity(downloadsActivityIntent);

            } else {
                Toast.makeText(button.getContext(), "Starting download. Please note that the notification may be bugged.", Toast.LENGTH_LONG).show();
                Downloader downloader = new Downloader();
                downloader.download(values);
            }

        });

        button.setOnClickListener(v -> {
            if (buttonMode == 0) {
                ReadValueHolder.currentChapter = chapterInfo.getValuesForChapters();
                Intent readIntent = new Intent(button.getContext(), ReadActivity.class);
                readIntent.putExtra("downloaded", ChaptersActivity.isDownloaded);
                button.getContext().startActivity(readIntent);
            } else {
                //List<Button> newList = values.stream().map(ButtonValuesChapterScreen::getSelectedButton).collect(Collectors.toList());
                List<String> newList = values.stream().map(ButtonValuesChapterScreen::getSelectedButtonUrl).collect(Collectors.toList());

                if (!newList.contains(chapterInfo.getValuesForChapters().url)) {
                    values.add(new ButtonValuesChapterScreen(button, chapterInfo.getValuesForChapters().url, chapterInfo.getValuesForChapters(), chapterInfo.getExtraData()));
                    this.button.setTextColor(Color.BLUE);
                } else {
                    int index = 0;
                    for (ButtonValuesChapterScreen i : values) {
                        if (Objects.equals(i.getSelectedButtonUrl(), chapterInfo.getValuesForChapters().url)) {
                            break;
                        }
                        index++;
                    }
                    values.remove(index);
                    button.setTextColor(getButtonColor(button, chapterInfo.getValuesForChapters().url, false));
                }
            }
        });

        button.setOnLongClickListener(v -> {
            buttonMode = 1;

            values.add(new ButtonValuesChapterScreen(button, chapterInfo.getValuesForChapters().url, chapterInfo.getValuesForChapters(), chapterInfo.getExtraData()));

            this.button.setTextColor(Color.BLUE);

            Activity activity = (Activity) this.button.getContext();
            Intent intent = activity.getIntent();

            // We want to make this an un-download button if the previous activity was the downloads activity
            if (intent.getBooleanExtra("downloaded", false)) {
                staticDownloadButton.setText("Remove from downloads");
                staticRemoveDownloadsWithImages.setVisibility(View.VISIBLE);
            }
            staticDownloadButton.setVisibility(View.VISIBLE);

            staticReadUnreadButton.setVisibility(View.VISIBLE);


            return true;
        });
    }


}


