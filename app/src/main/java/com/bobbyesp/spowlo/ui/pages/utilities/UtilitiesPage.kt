package com.bobbyesp.spowlo.ui.pages.utilities

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lyrics
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.bobbyesp.spowlo.R
import com.bobbyesp.spowlo.ui.common.LocalNavController
import com.bobbyesp.spowlo.ui.common.LocalPlayerAwareWindowInsets
import com.bobbyesp.spowlo.ui.common.Route
import com.bobbyesp.spowlo.ui.components.cards.AppUtilityCard

@Composable
fun UtilitiesPage() {
    val navController = LocalNavController.current
    val bottomInsetsAsPadding =
        LocalPlayerAwareWindowInsets.current.asPaddingValues().calculateBottomPadding()

    Scaffold { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(bottom = bottomInsetsAsPadding)
        ) {
            Column(
                modifier = Modifier,
            ) {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(150.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(8.dp)
                ) {
                    item {
                        AppUtilityCard(
                            utilityName = stringResource(id = R.string.lyrics_downloader),
                            icon = Icons.Default.Lyrics
                        ) {
                            navController.navigate(Route.LyricsDownloaderPage.route)
                        }
                    }
                }
            }
        }
    }
}