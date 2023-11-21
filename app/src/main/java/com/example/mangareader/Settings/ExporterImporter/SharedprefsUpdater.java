package com.example.mangareader.Settings.ExporterImporter;

import android.Manifest;
import android.app.Activity;
import android.content.*;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.util.Log;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;
import com.example.mangareader.Settings.Settings;
import com.google.gson.Gson;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static androidx.activity.result.ActivityResultCallerKt.registerForActivityResult;
import static androidx.core.app.ActivityCompat.startActivityForResult;

public class SharedprefsUpdater {
    private final Activity activity;
    private String encodedData;

    public SharedprefsUpdater(Activity activity) {
        this.activity = activity;
    }

    public void exportSettings() {
        SettingsToSaveGrabber grabber = new SettingsToSaveGrabber();
        ArrayList<SettingsToSave> settings = grabber.getSettingsToSave();
        StringBuilder sb = new StringBuilder();

        for (SettingsToSave i : settings) {
            sb.append(i.getName() + "::");

            String name = i.getName();
            String type = i.getType();

            if (type.equals("HashSet")) {
                SharedPreferences sharedpreferences = PreferenceManager.getDefaultSharedPreferences(this.activity);

                LinkedHashSet<String> set = new LinkedHashSet<>(
                        sharedpreferences.getStringSet(name, new LinkedHashSet<>()));

                for (String y : set) {
                    sb.append(y);
                    sb.append("__");
                }
                sb.append("::");
            }
        }

        // We encode the string to base64
        this.encodedData = Base64.getEncoder().encodeToString(sb.toString().getBytes(StandardCharsets.UTF_8));
        // Now we copy this data to the clipboard
        ClipboardManager clipboard = (ClipboardManager) this.activity.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("data", this.encodedData);
        clipboard.setPrimaryClip(clip);

        Toast.makeText(this.activity, "Settings have been exported", Toast.LENGTH_SHORT).show();


    }

    public void importSettings() {
        ClipboardManager clipboard = (ClipboardManager)activity.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData.Item item = clipboard.getPrimaryClip().getItemAt(0);

        String encodedData = item.getText().toString();

        String decodedData;
        try {
            decodedData = new String(Base64.getDecoder().decode(encodedData), StandardCharsets.UTF_8);
        }
        catch (IllegalArgumentException ex) {
            Toast.makeText(this.activity, "It appears your data you have copied is invalid", Toast.LENGTH_SHORT).show();
            return;
        }


        try {
            String[] parts = decodedData.split("::");
            HashMap<String, Set<String>> data = new HashMap<>();
            ArrayList<String> keys = new ArrayList<>();
            ArrayList<String> values = new ArrayList<>();

            int index = 0;
            for (String i : parts) {
                if (index == parts.length) {
                    break;
                }

                if (index % 2 == 0) {
                    keys.add(i);
                }
                else {
                    values.add(i);
                }
                index++;
            }

            for (String i : keys) {
                Set<String> mySet = new HashSet<>();
                String[] myValues = values.get(keys.indexOf(i)).split("__");
                for (String y : myValues) {
                    mySet.add(y.replace("__", ""));
                }
                data.put(i, mySet);
            }

            SharedPreferences sharedpreferences = PreferenceManager.getDefaultSharedPreferences(this.activity);
            SharedPreferences.Editor editor = sharedpreferences.edit();

            // We want to clear the sharedpreferences first before adding our new ones
            SettingsToSaveGrabber grabber = new SettingsToSaveGrabber();
            ArrayList<SettingsToSave> settingsToClear = grabber.getSettingsToSave();

            for (String i : data.keySet()) {
                // First we retrieve the initial data
                Set<String> initialData = sharedpreferences.getStringSet(i, new HashSet<>());
                Set<String> newData = data.get(i);

                // Now we combine the two sets
                Set<String> combinedSet = new HashSet<>();
                combinedSet.addAll(initialData);
                combinedSet.addAll(newData);

                editor.putStringSet(i, combinedSet);
                editor.apply();
            }

            Toast.makeText(this.activity, "Settings have been imported", Toast.LENGTH_SHORT).show();
        }
        catch (Exception ex) {
            Toast.makeText(this.activity, "An unknown error has appeared whilst trying to import the settings", Toast.LENGTH_SHORT).show();
        }


    }





}


