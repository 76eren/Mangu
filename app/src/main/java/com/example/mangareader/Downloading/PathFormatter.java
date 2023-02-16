package com.example.mangareader.Downloading;

public class PathFormatter {
    public String getPath(String path) {
        /*
        *Android doesn't allow certain characters to be in paths
        *  So we have to filter those out
        */
        path = path.replaceAll("[\\s]+", "_");   // Replace one or more whitespace characters with underscore
        path = path.replaceAll("![^/]*", "");    // Remove exclamation marks and everything that follows, up to the next forward slash
        path = path.replaceAll(";|\\\\|\\*|\\?|<|>|\\|", "_");  // Replace semicolons, backslashes, asterisks, question marks, less than, greater than, and pipe characters with underscores
        path = path.replaceAll(":", "_");        // Replace colons with underscores
        return path;
    }

}
