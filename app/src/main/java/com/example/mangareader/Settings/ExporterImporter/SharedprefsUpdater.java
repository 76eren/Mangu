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


    }

    public void importSettings() {
        

    }





}


