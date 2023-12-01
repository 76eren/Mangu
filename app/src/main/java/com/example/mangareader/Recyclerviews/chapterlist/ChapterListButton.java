package com.example.mangareader.Recyclerviews.chapterlist;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.example.mangareader.Activities.ChaptersActivity;
import com.example.mangareader.Activities.ReadActivity;
import com.example.mangareader.Activities.fragments.ChapterlistButtonsFragment;
import com.example.mangareader.R;
import com.example.mangareader.Settings.ListTracker;
import com.example.mangareader.ValueHolders.DesignValueHolder;
import com.example.mangareader.ValueHolders.ReadValueHolder;

import java.util.ArrayList;
import java.util.Objects;

public class ChapterListButton extends RviewAdapterChapterlist.ViewHolder {

    public static final int TYPE = 1;
    private final FragmentManager fragmentManager;
    private final Button button;
    public static boolean staticFramentIsEnabled = false;

    public ArrayList<Button> enabledButtons = new ArrayList<>();

    public ChapterListButton(LayoutInflater inflater, @NonNull ViewGroup parent, int layoutResource, FragmentManager fragmentManager) {
        super(inflater, parent, layoutResource);
        this.button = this.itemView.findViewById(R.id.button);
        this.fragmentManager = fragmentManager;
    }

    public void bind (ChapterInfo chapterInfo) {
        button.setText(chapterInfo.getValuesForChapters().name);
        button.setTextColor(getButtonColor(button, chapterInfo.getValuesForChapters().url));



        button.setOnLongClickListener(v -> {
            if (!staticFramentIsEnabled) {
                staticFramentIsEnabled = true;

                // We turn the button to the selected colour
                this.setButtonColor(button, DesignValueHolder.buttonTextColorBlue);
                this.enabledButtons.add(button);

                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                ChapterlistButtonsFragment fragment = new ChapterlistButtonsFragment();
                fragmentTransaction.replace(R.id.activity_chapters_fragment, fragment);
                fragmentTransaction.commit();

                new Thread(() -> {
                    try {
                        Thread.sleep(200); // Super dirty but we wait for the fragment to be created

                        Objects.requireNonNull(fragment.getDownloadButton()).setOnClickListener(v1 -> {
                            // TODO: implement this


                        });

                        Objects.requireNonNull(fragment.getReadUnreadButton()).setOnClickListener(v12 -> {
                            // todo: implement this

                        });


                    }
                    catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }).start();

            }


            return true;
        });

        button.setOnClickListener(v -> {
            if (staticFramentIsEnabled) {
                if (enabledButtons.contains(button)) {
                    enabledButtons.remove(button);
                    this.setButtonColor(button, getButtonColor(button, chapterInfo.getValuesForChapters().url));
                }
                else {
                    enabledButtons.add(button);
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