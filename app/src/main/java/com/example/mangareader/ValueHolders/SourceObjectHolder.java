package com.example.mangareader.ValueHolders;

import android.content.Context;
import com.example.mangareader.Settings;
import com.example.mangareader.SourceHandlers.Sources;
import com.example.mangareader.Sources.Mangadex;
import com.example.mangareader.Sources.Mangakakalot;
import com.example.mangareader.Sources.webtoons.Webtoons;

public class SourceObjectHolder {
    private static Sources sources;

    public static Sources getSources(Context context) {
        Settings settings = new Settings();
        String src = settings.ReturnValueString(context, "source", "mangadex");

        // The default value MUST reflect the default value of the root proferences!!!!
        switch (src) {
            case "mangakakalot":
                SourceObjectHolder.sources = new Mangakakalot();
                return sources;

            case "webtoons":
                SourceObjectHolder.sources = new Webtoons();
                return sources;

            default:
                SourceObjectHolder.sources = new Mangadex();
                return sources;
        }
    }

    public static void ChangeSource(Sources source, Context context) {
        Settings settings = new Settings();
        SourceObjectHolder.sources = source;
        settings.AssignValueString(context, "source", source.GetSourceName());
    }
}
