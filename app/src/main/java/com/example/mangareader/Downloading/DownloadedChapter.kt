package com.example.mangareader.Downloading

data class DownloadedChapter(
    var source: String,
    var url: String,
    var image: String,
    var name: String,
    val date: Int,
    val referer: String?, // Can be null
    val mangaStory: String,
    var chapterName: String,
    val imageNames: Array<String>,
    val imagesPath: String,
    val chapterNamesDefaultOrder: Array<String>
) 