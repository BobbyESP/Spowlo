package com.bobbyesp.spowlo.ui.pages.utilities.lyrics_downloader

import androidx.lifecycle.ViewModel
import com.bobbyesp.spowlo.features.spotifyApi.data.remote.SpotifyApiRequests
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LyricsDownloaderPageViewModel @Inject constructor(): ViewModel() {
    private val TAG = "LyricsDownloaderPageViewModel"
    private val api = SpotifyApiRequests

}