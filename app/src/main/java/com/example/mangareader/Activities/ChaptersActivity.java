package com.example.mangareader.Activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.mangareader.Downloading.Downloader;
import com.example.mangareader.R;
import com.example.mangareader.Recyclerviews.chapterlist.ChapterInfo;
import com.example.mangareader.Recyclerviews.chapterlist.ChapterListButton;
import com.example.mangareader.Recyclerviews.chapterlist.HeaderInfo;
import com.example.mangareader.Recyclerviews.chapterlist.RviewAdapterChapterlist;
import com.example.mangareader.Settings.ListTracker;
import com.example.mangareader.SourceHandlers.Sources;
import com.example.mangareader.SplashScreen;
import com.example.mangareader.ValueHolders.ReadValueHolder;
import com.example.mangareader.ValueHolders.SourceObjectHolder;
import org.json.JSONException;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;

public class ChaptersActivity extends AppCompatActivity {
    public static String url;
    public ArrayList<Sources.ValuesForChapters> dataChapters = new ArrayList<>();
    RviewAdapterChapterlist adapter;
    ArrayList<ChapterInfo> items = new ArrayList<>();
    private final HashMap<String, Object> extraData = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_chapters);

        overridePendingTransition(0, 0);

        // First we retrieve the url
        Intent intent = getIntent();

        String mangaUrl = "";
        String imageUrl = "";
        String mangaName = "";
        String referer = null;
        boolean downloaded = false;

        try {
            // Make sure these are not null
            mangaUrl = intent.getStringExtra("url");
            imageUrl = intent.getStringExtra("img");
            mangaName = intent.getStringExtra("mangaName");
            referer = intent.getStringExtra("referer");
        }
        catch (Exception ex) {
            Intent x = new Intent(this, HomeActivity.class);
            startActivity(x);
        }
        if (mangaUrl == null || imageUrl == null || mangaName == null) {
            Intent x = new Intent(this, HomeActivity.class);
            startActivity(x);
        }

        TextView Splashscreen = findViewById(R.id.Splashscreen);
        Splashscreen.setText(SplashScreen.returnQuote());

        String finalMangaUrl = mangaUrl;
        String finalMangaName = mangaName;
        String finalImageUrl = imageUrl;
        String finalReferer = referer;
        String finalMangaName1 = mangaName;

        Activity activity = this;
        new Thread(() -> {
            Sources sources = SourceObjectHolder.getSources(this);
            String mangaStory;
            mangaStory = sources.getStory(finalMangaUrl);


            // So this is probably very unnecessary... for now
            // I made this hashmap in case we ever come across a situation where we need any of these values
            // e.g for now the only value being used is the mangaName, this is being used by the Webtoons source
            // I don't really like the way this works too much, but it is what it is.

            this.extraData.put("mangaName", finalMangaName1);
            this.extraData.put("imageUrl", finalImageUrl);
            this.extraData.put("referer", finalReferer);
            this.extraData.put("mangaUrl", finalMangaUrl);
            this.extraData.put("mangaStory", mangaStory);
            try {
                dataChapters = sources.getChapters(finalMangaUrl, activity, this.extraData);

                ArrayList<String> chapterNamesDefaultOrderArraylist = new ArrayList<>();
                for (Sources.ValuesForChapters i : dataChapters) {
                    chapterNamesDefaultOrderArraylist.add(i.name);
                }
                String[] chapterNamesDefaultOrder = chapterNamesDefaultOrderArraylist.toArray(new String[0]);
                extraData.put("chapterNamesDefaultOrder", chapterNamesDefaultOrder);

            } catch (IOException | NoSuchAlgorithmException | InvalidKeyException | JSONException e) {
                throw new RuntimeException(e);
            }

            // We just want to restart if this is null, so we don't run into errors later on
            if (dataChapters == null) {
                Intent x = new Intent(this, HomeActivity.class);
                startActivity(x);
            }


            ReadValueHolder.ChaptersActivityData = dataChapters; // LOL imagine assigning values statically lol

            for (Sources.ValuesForChapters chapterData : dataChapters) {
                ChapterInfo chapterInfo = new ChapterInfo(chapterData, extraData, activity);
                this.items.add(chapterInfo);
            }

            activity.runOnUiThread(() -> {
                RecyclerView recyclerView = findViewById(R.id.rviewChapters);
                recyclerView.setLayoutManager(new LinearLayoutManager(activity));
                adapter = new RviewAdapterChapterlist(
                        activity,
                        new HeaderInfo(
                                finalMangaName,
                                finalMangaUrl,
                                finalImageUrl,
                                mangaStory,
                                finalReferer, // may be null
                                extraData
                        ),
                        this.items
                );
                recyclerView.setAdapter(adapter);
                Splashscreen.setVisibility(View.INVISIBLE);
            });

        }).start();

    }


    private void resetButtons() {
        for (ChapterInfo chapterInfo : this.items) {
            ChapterListButton chapterListButton = chapterInfo.getChapterListButton();
            if (chapterListButton == null) {
                continue;
            }
            if (chapterListButton.enabledButtons == null) {
                continue;
            }

            for (Button i : chapterListButton.enabledButtons) {
                // Put back the default colour
                i.setTextColor(ChapterListButton.getButtonColor(i, chapterInfo.getValuesForChapters().url));

            }
            chapterListButton.enabledButtons.clear();
            chapterListButton.valuesForChaptersList.clear();

        }
        ChapterListButton.staticShouldEnableToolbar = false;
    }


    @Override
    public void onBackPressed() {
        if (ChapterListButton.staticShouldEnableToolbar) {
            resetButtons();
        }
        else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.chaptersactivity_toolbar_menu, menu);
        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.chaptersactivity_action_toolbar_download:
                ArrayList<Sources.ValuesForChapters> thingsToDownload = new ArrayList<>();

                for (ChapterInfo chapterInfo : this.items) {
                    ChapterListButton chapterListButton = chapterInfo.getChapterListButton();
                    if (chapterListButton == null) {
                        continue;
                    }
                    if (chapterListButton.enabledButtons == null) {
                        continue;
                    }
                    thingsToDownload.addAll(chapterListButton.valuesForChaptersList);
                }
                resetButtons();

                for (Sources.ValuesForChapters i : thingsToDownload) {
                    i.extraData = this.extraData;
                    i.activity = this;
                }

                Downloader downloader = new Downloader();
                downloader.download(thingsToDownload, this);


                return true;

            case R.id.chaptersactivity_action_toolbar_read_unread:
                for (ChapterInfo chapterInfo : this.items) {
                    ChapterListButton chapterListButton = chapterInfo.getChapterListButton();
                    if (chapterListButton == null) {
                        continue;
                    }
                    if (chapterListButton.enabledButtons == null) {
                        continue;
                    }

                    for (Sources.ValuesForChapters i : chapterListButton.valuesForChaptersList) {
                        ListTracker.changeStatus(this, i.url, "History");
                    }
                }
                resetButtons();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    // THIS MIGHT BE USEFULLY LATER ON
//    private ArrayList<DownloadedChapter> sortingOptionOne(ArrayList<DownloadedChapter> relevantDownloads) {
//        // Now we sort the relevantDownloads
//        // For example we want to sort this list from ["Chapter 1", "Chapter 3", "Chapter 1.1"] to ["Chapter 1", "Chapter 1.1", "Chapter 3"]
//        ArrayList<String> relevantDownloadsChapterNames = null;
//        relevantDownloadsChapterNames = (ArrayList<String>) relevantDownloads.stream()
//                .map(DownloadedChapter::getChapterName)
//                .collect(Collectors.toList());
//
//        // This gets the latest relevantDownloadsChapterNamesArray there is available
//        // Because the relevantDownloadsChapterNamesArray for each object never gets updated
//        // It'll mess up if a new manga chapter every comes out
//        // In order to fix this we'll always target the relevantDownloadsChapterNamesArray with the latest object DownloadedChapter object creation (so the highest int date)
//        DownloadedChapter latestDownload = relevantDownloads.stream()
//                .max(Comparator.comparingInt(DownloadedChapter::getDate))
//                .orElse(null);
//        String[] relevantDownloadsChapterNamesArray = latestDownload.getChapterNamesDefaultOrder();
//
//        Collections.sort(relevantDownloadsChapterNames, Comparator.comparingInt(s -> Arrays.asList(relevantDownloadsChapterNamesArray).indexOf(s))); // magic
//        ArrayList<DownloadedChapter> sortedRelevantDownloads = new ArrayList<>();
//        for (String i : relevantDownloadsChapterNames) {
//            relevantDownloads.stream()
//                    .filter(DownloadedChapter -> i.equals(DownloadedChapter.getChapterName()))
//                    .findFirst()
//                    .ifPresent(sortedRelevantDownloads::add);
//        }
//
//        return sortedRelevantDownloads;
//    }
//
//    private ArrayList<DownloadedChapter> sortingOptionTwo(ArrayList<DownloadedChapter> relevantDownloads) {
//        // if for whatever reason the sorting fails we'll use this fallback as a second sorting option
//        // https://stackoverflow.com/questions/13973503/sorting-strings-that-contains-number-in-java
//        // Right now this code doesn't work correctly.
//        // For example, it messes up with floats.
//        // This function is a hit or miss really and that's also the reason why I'm using it as a plan B.
//        ArrayList<String> relevantDownloadsChapterNames = null;
//        Collections.sort(relevantDownloadsChapterNames, new Comparator<String>() {
//            @Override
//            public int compare(String o1, String o2) {
//                return extractInt(o1) - extractInt(o2);
//            }
//
//            int extractInt(String s) {
//                String num = s.replaceAll("\\D", "");
//                // return 0 if no digits found
//                return num.isEmpty() ? 0 : Integer.parseInt(num);
//            }
//        });
//
//        ArrayList<DownloadedChapter> sortedRelevantDownloads = new ArrayList<>();
//        for (String i : relevantDownloadsChapterNames) {
//            relevantDownloads.stream()
//                    .filter(DownloadedChapter -> i.equals(DownloadedChapter.getChapterName()))
//                    .findFirst()
//                    .ifPresent(sortedRelevantDownloads::add);
//        }
//
//        return sortedRelevantDownloads;
//    }


}