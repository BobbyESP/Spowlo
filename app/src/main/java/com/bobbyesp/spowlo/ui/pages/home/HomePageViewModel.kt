package com.bobbyesp.spowlo.ui.pages.home

import androidx.lifecycle.ViewModel
import com.bobbyesp.spowlo.features.spotifyApi.data.remote.SpotifyApiRequests
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomePageViewModel @Inject constructor(): ViewModel() {

    private val api = SpotifyApiRequests

}