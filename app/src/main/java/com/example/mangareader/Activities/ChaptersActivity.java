package com.example.mangareader.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.mangareader.Downloading.DownloadTracker;
import com.example.mangareader.Downloading.DownloadedChapter;
import com.example.mangareader.R;
import com.example.mangareader.Recyclerviews.chapterlist.ChapterInfo;
import com.example.mangareader.Recyclerviews.chapterlist.ChapterListButton;
import com.example.mangareader.Recyclerviews.chapterlist.HeaderInfo;
import com.example.mangareader.Recyclerviews.chapterlist.RviewAdapterChapterlist;
import com.example.mangareader.SourceHandlers.Sources;
import com.example.mangareader.SplashScreen;
import com.example.mangareader.ValueHolders.ReadValueHolder;
import com.example.mangareader.ValueHolders.SourceObjectHolder;
import org.json.JSONException;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.stream.Collectors;

public class ChaptersActivity extends AppCompatActivity {
    public static String url;
    public static ArrayList<Sources.ValuesForChapters> dataChapters = new ArrayList<>();
    public static boolean isDownloaded = false;
    RviewAdapterChapterlist adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_chapters);

        getSupportActionBar().setDisplayShowTitleEnabled(false);
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
            downloaded = intent.getBooleanExtra("downloaded", false);
            isDownloaded = downloaded;
            imageUrl = intent.getStringExtra("img");
            mangaName = intent.getStringExtra("mangaName");
            referer = intent.getStringExtra("referer");
        } catch (Exception ex) {
            Intent x = new Intent(this, HomeActivity.class);
            startActivity(x);
        }
        if (mangaUrl == null || imageUrl == null || mangaName == null) {
            Intent x = new Intent(this, HomeActivity.class);
            startActivity(x);
        }

        TextView Splashscreen = findViewById(R.id.Splashscreen);
        Splashscreen.setText(SplashScreen.returnQuote());

        Activity activity = this;

        String finalMangaUrl = mangaUrl;
        String finalMangaName = mangaName;
        String finalImageUrl = imageUrl;
        String finalReferer = referer;
        String finalMangaName1 = mangaName;

        ArrayList<DownloadedChapter> relevantDownloads = new ArrayList<>();
        if (downloaded) {
            // If downloaded is true we want to get all the relevant objects
            DownloadTracker downloadTracker = new DownloadTracker();
            LinkedHashSet<DownloadedChapter> downloads = downloadTracker.getFromDownloads(this);
            // Now we only want the relevant objects
            for (DownloadedChapter i : downloads) {
                if (i.getName().trim().equals(mangaName.trim())) {
                    relevantDownloads.add(i);
                }
            }

            // This does the actual sorting
            try {
                ArrayList<DownloadedChapter> sorted = sortingOptionOne(relevantDownloads);
                relevantDownloads.clear();
                relevantDownloads.addAll(sorted);
            } catch (Exception ex) {
                try {
                    ArrayList<DownloadedChapter> sorted = sortingOptionTwo(relevantDownloads);
                    relevantDownloads.clear();
                    relevantDownloads.addAll(sorted);
                } catch (Exception error) {

                }

            }
        }


        boolean finalDownloaded = downloaded;
        new Thread(() -> {
            Sources sources = SourceObjectHolder.getSources(this);
            String mangaStory;

            if (!finalDownloaded) {
                mangaStory = sources.getStory(finalMangaUrl);
            } else {
                // The mangastory is for every object the same, so we can just get the first object in our arraylist
                mangaStory = relevantDownloads.get(0).getMangaStory();
            }


            // So this is probably very unnecessary... for now
            // I made this hashmap in case we ever come across a situation where we need any of these values
            // e.g for now the only value being used is the mangaName, this is being used by the Webtoons source
            // I don't really like the way this works too much, but it is what it is.
            HashMap<String, Object> extraData = new HashMap<>();
            extraData.put("mangaName", finalMangaName1);
            extraData.put("imageUrl", finalImageUrl);
            extraData.put("referer", finalReferer);
            extraData.put("mangaUrl", finalMangaUrl);
            extraData.put("mangaStory", mangaStory);
            if (!finalDownloaded) {
                try {
                    dataChapters = sources.getChapters(finalMangaUrl, activity, extraData);
                    ArrayList<String> chapterNamesDefaultOrderArraylist = new ArrayList<>();
                    for (Sources.ValuesForChapters i : dataChapters) {
                        chapterNamesDefaultOrderArraylist.add(i.name);
                    }
                    String[] chapterNamesDefaultOrder = chapterNamesDefaultOrderArraylist.toArray(new String[0]);
                    extraData.put("chapterNamesDefaultOrder", chapterNamesDefaultOrder);
                } catch (IOException | NoSuchAlgorithmException | InvalidKeyException | JSONException e) {
                    throw new RuntimeException(e);
                }
            } else {
                for (DownloadedChapter i : relevantDownloads) {
                    Sources.ValuesForChapters valuesForChapters = new Sources.ValuesForChapters();
                    valuesForChapters.name = i.getChapterName();
                    valuesForChapters.url = i.getUrl();
                    valuesForChapters.extraData = extraData;

                    boolean canAdd = true;
                    for (Sources.ValuesForChapters y : dataChapters) {
                        if (y.url.equals(valuesForChapters.url)) {
                            canAdd = false;
                            break;
                        }
                    }
                    if (canAdd) {
                        dataChapters.add(valuesForChapters);
                    }

                }
            }

            // We just want to restart if this is null, so we don't run into errors later on
            if (dataChapters == null) {
                Intent x = new Intent(this, HomeActivity.class);
                startActivity(x);
            }

            // Yay more statics
            ChapterListButton.staticDownloadButton = findViewById(R.id.DownloadButton);
            ChapterListButton.staticReadUnreadButton = findViewById(R.id.ReadUnreadButton);
            ChapterListButton.staticRemoveDownloadsWithImages = findViewById(R.id.removeDownloadsWithImages);


            ReadValueHolder.ChaptersActivityData = dataChapters; // LOL imagine assigning values statically lol
            List<ChapterInfo> items = new ArrayList<>();
            for (Sources.ValuesForChapters chapterData : dataChapters) {
                ChapterInfo chapterInfo = new ChapterInfo(chapterData, extraData);
                items.add(chapterInfo);
            }


            activity.runOnUiThread(() -> {

                RecyclerView recyclerView = findViewById(R.id.rviewDownloads);
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
                        items
                );
                recyclerView.setAdapter(adapter);

                Splashscreen.setVisibility(View.INVISIBLE);
            });

        }).start();

    }

    // I decided to do things statically because the chapterListButton object isn't in this activity
    // I hate working with statics like this A LOT, but this seemed like the least pain in the ass.
    @Override
    public void onBackPressed() {
        if (ChapterListButton.getButtonMode() == 1) {
            ChapterListButton.resetButtons();
            ChapterListButton.setButtonMode(0);
        } else {
            dataChapters = new ArrayList<>(); // We are using a bunch of statics, so we need to clear the variables manually if we have no use for them.
            if (!isDownloaded) {
                this.finish();
            } else {
                Intent intent = new Intent(this, DownloadsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }

        }
    }

    private ArrayList<DownloadedChapter> sortingOptionOne(ArrayList<DownloadedChapter> relevantDownloads) {
        // Now we sort the relevantDownloads
        // For example we want to sort this list from ["Chapter 1", "Chapter 3", "Chapter 1.1"] to ["Chapter 1", "Chapter 1.1", "Chapter 3"]
        ArrayList<String> relevantDownloadsChapterNames = null;
        relevantDownloadsChapterNames = (ArrayList<String>) relevantDownloads.stream()
                .map(DownloadedChapter::getChapterName)
                .collect(Collectors.toList());

        // This gets the latest relevantDownloadsChapterNamesArray there is available
        // Because the relevantDownloadsChapterNamesArray for each object never gets updated
        // It'll mess up if a new manga chapter every comes out
        // In order to fix this we'll always target the relevantDownloadsChapterNamesArray with the latest object DownloadedChapter object creation (so the highest int date)
        DownloadedChapter latestDownload = relevantDownloads.stream()
                .max(Comparator.comparingInt(DownloadedChapter::getDate))
                .orElse(null);
        String[] relevantDownloadsChapterNamesArray = latestDownload.getChapterNamesDefaultOrder();

        Collections.sort(relevantDownloadsChapterNames, Comparator.comparingInt(s -> Arrays.asList(relevantDownloadsChapterNamesArray).indexOf(s))); // magic
        ArrayList<DownloadedChapter> sortedRelevantDownloads = new ArrayList<>();
        for (String i : relevantDownloadsChapterNames) {
            relevantDownloads.stream()
                    .filter(DownloadedChapter -> i.equals(DownloadedChapter.getChapterName()))
                    .findFirst()
                    .ifPresent(sortedRelevantDownloads::add);
        }

        return sortedRelevantDownloads;
    }

    private ArrayList<DownloadedChapter> sortingOptionTwo(ArrayList<DownloadedChapter> relevantDownloads) {
        // if for whatever reason the sorting fails we'll use this fallback as a second sorting option
        // https://stackoverflow.com/questions/13973503/sorting-strings-that-contains-number-in-java
        // Right now this code doesn't work correctly.
        // For example, it messes up with floats.
        // This function is a hit or miss really and that's also the reason why I'm using it as a plan B.
        ArrayList<String> relevantDownloadsChapterNames = null;
        Collections.sort(relevantDownloadsChapterNames, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return extractInt(o1) - extractInt(o2);
            }

            int extractInt(String s) {
                String num = s.replaceAll("\\D", "");
                // return 0 if no digits found
                return num.isEmpty() ? 0 : Integer.parseInt(num);
            }
        });

        ArrayList<DownloadedChapter> sortedRelevantDownloads = new ArrayList<>();
        for (String i : relevantDownloadsChapterNames) {
            relevantDownloads.stream()
                    .filter(DownloadedChapter -> i.equals(DownloadedChapter.getChapterName()))
                    .findFirst()
                    .ifPresent(sortedRelevantDownloads::add);
        }

        return sortedRelevantDownloads;
    }


}