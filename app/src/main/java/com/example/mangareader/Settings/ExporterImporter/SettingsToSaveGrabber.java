package com.example.mangareader.Settings.ExporterImporter;

import java.util.ArrayList;

public class SettingsToSaveGrabber {
    public ArrayList<SettingsToSave> getSettingsToSave() {
        // HERE WE STORE ALL THE SETTINGS WE WANT TO SAVE

        ArrayList<SettingsToSave> settingsToSaves = new ArrayList<>();
        settingsToSaves.add(new SettingsToSave("Favourites", "HashSet", null));
        settingsToSaves.add(new SettingsToSave("History", "HashSet", null));

        return settingsToSaves;
    }


}
