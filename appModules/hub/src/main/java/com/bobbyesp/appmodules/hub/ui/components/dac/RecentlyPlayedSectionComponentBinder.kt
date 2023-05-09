package com.bobbyesp.appmodules.hub.ui.components.dac

import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import com.bobbyesp.appmodules.hub.ui.dac.DacComponentRendererViewModel

@Composable
fun RecentlyPlayedSectionComponentBinder(
    dacComponentRendererViewModel: DacComponentRendererViewModel
) {/*
    var recentlyPlayed: UiResponse
    LaunchedEffect(true) {
        recentlyPlayed = dacComponentRendererViewModel.getRecentlyPlayed()
    }*/
    val lazyListState = rememberLazyListState()


}