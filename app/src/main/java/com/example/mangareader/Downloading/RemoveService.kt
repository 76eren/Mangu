package com.example.mangareader.Downloading

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.mangareader.R
import com.example.mangareader.SourceHandlers.Sources.ValuesForChapters
import java.io.File
import java.util.Arrays
import java.util.concurrent.Executors

class RemoveService : Service() {

    companion object {
        var staticValues: ArrayList<ValuesForChapters> = ArrayList()
        var staticDownloads: ArrayList<DownloadedChapter> = ArrayList()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notifications = NotificationManagerCompat.from(this)
        notifications.createNotificationChannel(
            NotificationChannel(
                "mangu_remove",
                "Removing downloads",
                NotificationManager.IMPORTANCE_MIN
            )
        )
        val builder: NotificationCompat.Builder = NotificationCompat.Builder(this, "mangu_remove")
            .setSmallIcon(R.drawable.download_icon)
            .setContentTitle("Removing downloaded manga")
            .setContentText("Removing selected chapters")
        startForeground(123, builder.build())

        val downloads: ArrayList<DownloadedChapter> = ArrayList(staticDownloads)
        val values: ArrayList<ValuesForChapters> = ArrayList(staticValues)

        staticValues.clear()
        staticDownloads.clear()


        val executor = Executors.newSingleThreadExecutor()
        executor.execute {
            for (i in values) {
                var targetChapter: DownloadedChapter? = null
                for (y in downloads) {
                    if (y.url == i.url) {
                        targetChapter = y
                        break
                    }
                }
                if (targetChapter != null) {
                    val directory = File(targetChapter.imagesPath)
                    val files = directory.listFiles()
                    if (files != null) {
                        for (file in files) {
                            if (file == null) continue
                            file.delete()
                        }
                    }
                }
            }
        }
        stopSelf()
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

}