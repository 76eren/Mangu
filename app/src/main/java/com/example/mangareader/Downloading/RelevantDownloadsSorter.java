package com.example.mangareader.Downloading;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.stream.Collectors;

public class RelevantDownloadsSorter {

    public ArrayList<DownloadedChapter> sortingOptionOne(ArrayList<DownloadedChapter> relevantDownloads) {
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

    public ArrayList<DownloadedChapter> sortingOptionTwo(ArrayList<DownloadedChapter> relevantDownloads) {
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
