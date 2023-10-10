package com.example.mangareader.Downloading;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.os.Environment;
import android.os.IBinder;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import com.example.mangareader.R;
import com.example.mangareader.Recyclerviews.chapterlist.ButtonValuesChapterScreen;
import com.example.mangareader.SourceHandlers.Sources;
import com.example.mangareader.ValueHolders.SourceObjectHolder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DownloadService extends Service {
    private static CopyOnWriteArrayList<ButtonValuesChapterScreen> values;

    public static void setValues(CopyOnWriteArrayList<ButtonValuesChapterScreen> values) {
        DownloadService.values = values;
    }


    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        NotificationManagerCompat notifications = NotificationManagerCompat.from(this);
        notifications.createNotificationChannel(new NotificationChannel("mangu_download", "Downloads", NotificationManager.IMPORTANCE_MIN));
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "mangu_download")
                .setSmallIcon(R.drawable.download_icon)
                .setContentTitle("Download")
                .setProgress(100, 0, true)
                .setContentText("Download");

        startForeground(123, builder.build());

        CopyOnWriteArrayList<ButtonValuesChapterScreen> myValues = new CopyOnWriteArrayList<>(values);

        ExecutorService executor = Executors.newSingleThreadExecutor();

        Sources source = SourceObjectHolder.getSources(getApplicationContext());

        DownloadTracker downloadTracker = new DownloadTracker();

        executor.execute(() -> {


            for (ButtonValuesChapterScreen i : myValues) {

                ArrayList<String> images = source.getImages(i.getValuesForChapters(), i.getSelectedButton().getContext());
                images.removeAll(Collections.singleton(""));

                String chapterUrl = i.getSelectedButtonUrl();
                HashMap<String, String> reqData = source.getRequestData(chapterUrl);

                int index = 1;
                String[] imageNames = new String[images.size()];
                String imagesPath = "";
                for (String imageUrl : images) {
                    // The notification
                    float progress = index / (float) images.size() * 100;

                    builder.setProgress(100, (int) progress, false);

                    notifications.notify(123, builder.build());
                    builder.setContentTitle("Downloads: " + (myValues.indexOf(i) + 1) + " out of " + myValues.size());
                    imageNames[index - 1] = imageUrl;

                    //https://stackoverflow.com/questions/18210700/best-method-to-download-image-from-url-in-android
                    URL url = null;
                    try {
                        url = new URL(imageUrl);
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                    Bitmap bm = null;
                    try {
                        URLConnection conn = url.openConnection();

                        // On mangadex the Referer needs to be null to actually download images
                        // The problem is that if we put the Referer as null in Mangadex.getRequestData it'll break some parts of the app
                        if (reqData.get("noRefererWhenDownloading") == "true") { // noRefererWhenDownloading might be null, so we use == instead of .equals
                            conn.setRequestProperty("Referer", null);
                        }
                        else {
                            conn.setRequestProperty("Referer", reqData.get("Referer"));
                        }

                        bm = BitmapFactory.decodeStream(conn.getInputStream());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    //Create Path to save Image
                    PathFormatter pathFormatter = new PathFormatter();
                    String name = "Mangu_" + i.getExtraData().get("mangaName") + "_" + i.getValuesForChapters().name;
                    name = pathFormatter.getPath(name);
                    File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES + "/" + name);
                    imagesPath = path.getPath();
                    imagesPath = pathFormatter.getPath(imagesPath);


                    if (!path.exists()) {
                        path.mkdirs();
                    }

                    File imageFile = new File(path, System.currentTimeMillis() + ".png");
                    imageNames[index - 1] = imageFile.getName();
                    FileOutputStream out = null;
                    try {
                        out = new FileOutputStream(imageFile);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    try {
                        bm.compress(Bitmap.CompressFormat.PNG, 100, out); // Compress Image
                        out.flush();
                        out.close();

                        // Tell the media scanner about the new file so that it is
                        // immediately available to the user.
                        MediaScannerConnection.scanFile(getApplicationContext(), new String[]{imageFile.getAbsolutePath()}, null, (path1, uri) -> {
                            // Log.i("ExternalStorage", "Scanned " + path + ":");
                            //    Log.i("ExternalStorage", "-> uri=" + uri);
                        });
                    } catch (Exception e) {
                    }
                    index++;
                }
                downloadTracker.addToDownloads(i, getApplicationContext(), imageNames, imagesPath);

            }
            builder.setContentTitle("Downloads: " + myValues.size() + " out of " + myValues.size());
        });

        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


}
