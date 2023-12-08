package com.example.mangareader.Downloading

class PathFormatter {
    fun getPath(path: String): String {
        /*
         *  Android doesn't allow certain characters to be in paths
         *  So we have to filter those out
         */
        var path = path
        path = path.replace("[\\s]+".toRegex(), "_") // Replace one or more whitespace characters with underscore
        path = path.replace("![^/]*".toRegex(), "") // Remove exclamation marks and everything that follows, up to the next forward slash
        path = path.replace(";|\\\\|\\*|\\?|<|>|\\|".toRegex(), "_") // Replace semicolons, backslashes, asterisks, question marks, less than, greater than, and pipe characters with underscores
        path = path.replace(":".toRegex(), "_") // Replace colons with underscores
        return path
    }
}