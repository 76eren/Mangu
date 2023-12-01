package com.example.mangareader.Activities.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.example.mangareader.R


class ChapterlistButtonsFragment : Fragment() {
    private lateinit var downloadButton: Button
    private lateinit var readUnreadButton: Button


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_chapterlist_buttons, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Initialize your buttons here
        downloadButton = view.findViewById(R.id.downloadButton)
        readUnreadButton = view.findViewById(R.id.readUnreadButton)
    }

    fun getDownloadButton(): Button? {
        if (!::downloadButton.isInitialized) {
            return null
        }
        return this.downloadButton
    }

    fun getReadUnreadButton(): Button? {
        if (!::readUnreadButton.isInitialized) {
            return null
        }

        return this.readUnreadButton
    }


}