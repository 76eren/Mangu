package com.example.mangareader.Downloading;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.stream.Collectors;

public class RelevantDownloadsSorter {

    public ArrayList<DownloadedChapter> sortingOptionOne(ArrayList<DownloadedChapter> relevantDownloads) {
        // Get the chapter names from the relevant downloads
        ArrayList<String> relevantDownloadsChapterNames = (ArrayList<String>) relevantDownloads.stream()
                .map(DownloadedChapter::getChapterName)
                .collect(Collectors.toList());

        // Get the latest download
        DownloadedChapter latestDownload = relevantDownloads.stream()
                .max(Comparator.comparingInt(DownloadedChapter::getDate))
                .orElse(null);

        // Get the default order of chapter names from the latest download
        String[] relevantDownloadsChapterNamesArray = latestDownload.getChapterNamesDefaultOrder();

        // Sort the chapter names based on the default order
        Collections.sort(relevantDownloadsChapterNames, Comparator.comparingInt(s -> Arrays.asList(relevantDownloadsChapterNamesArray).indexOf(s)));
        ArrayList<DownloadedChapter> sortedRelevantDownloads = new ArrayList<>();
        for (String i : relevantDownloadsChapterNames) {
            // Find the chapter that matches the current chapter name
            DownloadedChapter matchedChapter = relevantDownloads.stream()
                    .filter(DownloadedChapter -> i.equals(DownloadedChapter.getChapterName()))
                    .findFirst()
                    .orElse(null);
            if (matchedChapter != null) {
                // Add the matched chapter to the sorted list
                sortedRelevantDownloads.add(matchedChapter);
                // Remove the matched chapter from the list of relevant downloads
                relevantDownloads.remove(matchedChapter);
            }
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
