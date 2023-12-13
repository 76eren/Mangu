package com.example.mangareader.Downloading

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Environment
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.mangareader.R
import com.example.mangareader.SourceHandlers.Sources.ValuesForChapters
import com.example.mangareader.ValueHolders.SourceObjectHolder
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.net.MalformedURLException
import java.net.URL
import java.util.concurrent.Executors

class DownloadService : Service() {
    companion object {
        var thingsToDownload = ArrayList<ValuesForChapters>()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notifications = NotificationManagerCompat.from(this)
        notifications.createNotificationChannel(
            NotificationChannel(
                "mangu_download",
                "Downloads",
                NotificationManager.IMPORTANCE_MIN
            )
        )
        val builder: NotificationCompat.Builder = NotificationCompat.Builder(this, "mangu_download")
            .setSmallIcon(R.drawable.download_icon)
            .setContentTitle("Download")
            .setProgress(100, 0, true)
            .setContentText("Download")
        startForeground(123, builder.build())

        // We set up the neccesary variables to start the downloading process
        val source = SourceObjectHolder.getSources(applicationContext) // We get the source object
        val thingsToDownload = ArrayList(thingsToDownload) // We make a copy of the list, so we can remove items from it
        val downloadTracker: DownloadTracker = DownloadTracker()

        // We clear the static list
        DownloadService.thingsToDownload.clear()

        val executor = Executors.newSingleThreadExecutor()
        executor.execute {
            for (i in thingsToDownload) {
                // First we get all the images for the chapter
                val images: ArrayList<String> = source?.getImages(i, i.activity as Context) as ArrayList<String>

                if (images.isEmpty()) {
                    continue
                }

                images.removeAll(setOf(""))

                val chapterUrl: String = i.url
                val requestData: HashMap<String, String> = source.getRequestData(chapterUrl)

                var index = 1
                val imageNames = arrayOfNulls<String>(images.size)
                var imagesPath = ""
                for (imageUrl in images) {

                    // The notification
                    val progress: Float = index / images.size.toFloat() * 100

                    builder.setProgress(100, progress.toInt(), false)
                    notifications.notify(123, builder.build())
                    builder.setContentTitle("Downloads: " + (thingsToDownload.indexOf(i) + 1) + " out of " + thingsToDownload.size)
                    imageNames[index - 1] = imageUrl


                    //https://stackoverflow.com/questions/18210700/best-method-to-download-image-from-url-in-android
                    var url: URL? = null
                    try {
                        url = URL(imageUrl)
                    } catch (e: MalformedURLException) {
                        e.printStackTrace()
                    }
                    var bm: Bitmap? = null
                    try {
                        val conn = url?.openConnection() ?: continue

                        // On certain sources the Referer needs to be null to actually download images
                        // The problem is that if we put the Referer as null in source.getRequestData it'll break some parts of the app
                        if (requestData.get("noRefererWhenDownloading") === "true") { // noRefererWhenDownloading might be null, so we use == instead of .equals
                            conn.setRequestProperty("Referer", null)
                        }
                        else {
                            conn.setRequestProperty("Referer", requestData.get("Referer"))
                        }

                        bm = BitmapFactory.decodeStream(conn.getInputStream())
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }

                    val pathFormatter = PathFormatter()
                    var name = "Mangu_" + i.extraData["mangaName"] + "_" + i.name
                    name = pathFormatter.getPath(name)
                    val path =
                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES + "/mangu/" + name)
                    imagesPath = path.path
                    imagesPath = pathFormatter.getPath(imagesPath)

                    if (!path.exists()) {
                        path.mkdirs()
                    }

                    val imageFile = File(path, System.currentTimeMillis().toString() + ".png")
                    imageNames[index - 1] = imageFile.name
                    var out: FileOutputStream? = null
                    try {
                        out = FileOutputStream(imageFile)
                    }
                    catch (e: FileNotFoundException) {
                        e.printStackTrace()
                    }

                    try {
                        bm?.compress(Bitmap.CompressFormat.PNG, 100, out) ?: continue
                        out?.flush() ?: continue
                        out.close()

                        // Tell the media scanner about the new file so that it is immediately available to the user.
                        MediaScannerConnection.scanFile(applicationContext, arrayOf(imageFile.absolutePath), null) { path1: String?, uri: Uri? -> }
                    }
                    catch (_: Exception) { }
                    index++
                }
                downloadTracker.addToDownloads(i, applicationContext, imageNames as Array<String>, imagesPath)


            }
            builder.setContentTitle("Downloads: " + thingsToDownload.size + " out of " + thingsToDownload.size)
        }




        return START_STICKY
    }


}