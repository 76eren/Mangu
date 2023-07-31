package com.example.mangareader.Downloading;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import com.example.mangareader.R;
import com.example.mangareader.Recyclerviews.chapterlist.ButtonValuesChapterScreen;

import java.io.File;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RemoveService extends Service {
    private static CopyOnWriteArrayList<ButtonValuesChapterScreen> values;
    private static Set<DownloadedChapter> downloads;

    public static void setValues(CopyOnWriteArrayList<ButtonValuesChapterScreen> values) {
        RemoveService.values = new CopyOnWriteArrayList<>(values);
    }

    public static void setDownloads(Set<DownloadedChapter> set) {
        downloads = new LinkedHashSet<>(set);
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        NotificationManagerCompat notifications = NotificationManagerCompat.from(this);
        notifications.createNotificationChannel(new NotificationChannel("mangu_remove", "Removing downloads", NotificationManager.IMPORTANCE_MIN));
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "mangu_remove")
                .setSmallIcon(R.drawable.download_icon)
                .setContentTitle("Removing downloaded manga")
                .setContentText("Removing selected chapters");

        startForeground(123, builder.build());

        CopyOnWriteArrayList<ButtonValuesChapterScreen> myValues = new CopyOnWriteArrayList<>(values);

        ExecutorService executor = Executors.newSingleThreadExecutor();

        executor.execute(() -> {
            for (ButtonValuesChapterScreen i : myValues) {
                DownloadedChapter targetChapter = null;
                for (DownloadedChapter y : downloads) {
                    if (y.getUrl().equals(i.getSelectedButtonUrl())) {
                        targetChapter = y;
                        break;
                    }

                }

                // Thanks chatgpt, I truly love you
                // I truly cannot imagine a world without you anymore
                if (targetChapter != null) {
                    PathFormatter pathFormatter = new PathFormatter();
                    File directory = new File(targetChapter.getImagesPath());
                    File[] files = directory.listFiles();
                    for (File file : files) {
                        file.delete();
                    }
                }

            }
        });

        stopSelf();
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
