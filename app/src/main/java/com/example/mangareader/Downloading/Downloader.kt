package com.example.mangareader.Downloading

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.mangareader.SourceHandlers.Sources.ValuesForChapters
import java.util.ArrayList

class Downloader {

    fun remove() {

    }

    fun download(valuesForChapters: ArrayList<ValuesForChapters>, context: Context) {
        if (checkPermissions(context) == -1 || valuesForChapters.isEmpty()) {
            return
        }

        DownloadService.thingsToDownload = valuesForChapters
        val serviceIntent = Intent(context, DownloadService::class.java)
        context.startService(serviceIntent)

    }

    fun checkPermissions(context: Context): Int {
        val permissions_code = 123
        val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
            && ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions((context as Activity)!!, permissions, permissions_code)
        }

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
            && ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
        ) {
            Toast.makeText(context, "It appears the app does not have permissions to the storage. Please grant permissions in the settings", Toast.LENGTH_SHORT).show()
            return -1 // You may not continue
        }

        return 0 // You may continue
    }
}