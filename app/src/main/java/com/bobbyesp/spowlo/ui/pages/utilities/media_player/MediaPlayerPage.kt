package com.bobbyesp.spowlo.ui.pages.utilities.media_player

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun MediaPlayerPage(
    viewModel: MediaPlayerPageViewModel
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
        },
        topBar = {
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize().padding(paddingValues)
        ) {

        }
    }
}

@Composable
fun MediaPlayerPageControlButtons(
    onPlayPause : () -> Unit,
    onNext : () -> Unit,
    onPrevious : () -> Unit
) {

}