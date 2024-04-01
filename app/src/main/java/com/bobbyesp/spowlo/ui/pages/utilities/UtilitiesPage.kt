package com.bobbyesp.spowlo.ui.pages.utilities

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Lyrics
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.bobbyesp.spowlo.R
import com.bobbyesp.spowlo.ui.common.LocalNavController
import com.bobbyesp.spowlo.ui.common.LocalPlayerAwareWindowInsets
import com.bobbyesp.spowlo.ui.common.Route
import com.bobbyesp.spowlo.ui.components.cards.AppUtilityCard

@Composable
fun UtilitiesPage() {
    val navController = LocalNavController.current
    val bottomInsetsAsPadding = LocalPlayerAwareWindowInsets.current.asPaddingValues()

    var showMetadatorDialog by remember {
        mutableStateOf(false)
    }

    Scaffold { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(
                    bottom = bottomInsetsAsPadding.calculateBottomPadding(),
                    start = bottomInsetsAsPadding.calculateStartPadding(
                        LocalLayoutDirection.current
                    )
                )
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
                            navController.navigate(Route.LyricsDownloader.route)
                        }
                    }
                    item {
                        AppUtilityCard(
                            utilityName = stringResource(id = R.string.id3_tag_editor),
                            icon = Icons.Default.Edit
                        ) {
                            showMetadatorDialog = true
                        }
                    }
                }
            }
        }
    }
    if (showMetadatorDialog) {
        MetadatorDialog(onDismissRequest = {
            showMetadatorDialog = false
        }) {
            navController.navigate(Route.TagEditor.route)
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MetadatorDialog(
    modifier: Modifier = Modifier,
    onDismissRequest: () -> Unit = {},
    bypass: () -> Unit
) {
    val uriHandler = LocalUriHandler.current
    AlertDialog(modifier = modifier, title = {
        Text(text = stringResource(id = R.string.metadator), fontWeight = FontWeight.SemiBold)
    }, text = {
        Text(text = stringResource(id = R.string.metadator_description))
    }, onDismissRequest = {
        onDismissRequest()
    }, confirmButton = {
        Surface(
            modifier = Modifier
                .combinedClickable(
                    onLongClick = bypass,
                    onClick = { uriHandler.openUri("https://github.com/BobbyESP/Metadator/releases/latest") })
                .background(MaterialTheme.colorScheme.primaryContainer)
                .clip(shape = MaterialTheme.shapes.medium)
        ) {
            Text(modifier = Modifier.padding(6.dp), text = stringResource(id = R.string.download))
        }
    })
}