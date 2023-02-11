@file:OptIn(ExperimentalFoundationApi::class)

package com.bobbyesp.spowlo.ui.components.history

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.bobbyesp.spowlo.R
import com.bobbyesp.spowlo.ui.common.AsyncImageImpl
import com.bobbyesp.spowlo.ui.common.LocalWindowWidthState
import com.bobbyesp.spowlo.utils.toFileSizeText

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HistoryMediaItem(
    modifier: Modifier = Modifier,
    songName: String = "",
    author: String = "",
    artworkUrl: String = "",
    songPath: String = "",
    songSpotifyUrl: String = "",
    songFileSize: Long = 0L,
    isSelectEnabled: () -> Boolean = { false },
    isSelected: () -> Boolean = { false },
    onSelect: () -> Unit = {},
    onClick: () -> Unit = {},
    onLongClick: () -> Unit = {}
) {
    val imageWeight = when (LocalWindowWidthState.current) {
        WindowWidthSizeClass.Expanded -> {
            0.30f
        }

        WindowWidthSizeClass.Medium -> {
            0.20f
        }

        else -> {
            0.25f
        }
    }
    val haptic = LocalHapticFeedback.current
    val context = LocalContext.current
    val isFileAvailable = songFileSize != 0L
    val fileSizeText = songFileSize.toFileSizeText()

    Box(
        modifier = with(modifier) {
            if (!isSelectEnabled()) combinedClickable(
                enabled = true,
                onClick = { onClick() },
                onClickLabel = stringResource(R.string.open_file),
                onLongClick = {
                    onLongClick()
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                },
                onLongClickLabel = stringResource(R.string.show_more_actions)
            )
            else selectable(selected = isSelected(), onClick = onSelect)
        }.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            AnimatedVisibility(
                modifier = Modifier.align(Alignment.CenterVertically),
                visible = isSelectEnabled(),
            ) {
                Checkbox(
                    modifier = Modifier.padding(start = 4.dp, end = 16.dp),
                    checked = isSelected(),
                    onCheckedChange = null
                )
            }
            ArtworkImage(
                modifier = Modifier.weight(1f - imageWeight),
                imageModel = artworkUrl
            )
            Column(
                modifier = Modifier
                    .weight(1f - imageWeight)
                    .padding(horizontal = 12.dp)
                    .fillMaxWidth(), verticalArrangement = Arrangement.Top
            ) {
                
            }
        }
    }
}

@Composable
fun ArtworkImage(modifier: Modifier = Modifier, imageModel: String) {
    AsyncImageImpl(
        modifier = modifier
            .aspectRatio(
                1f, matchHeightConstraintsFirst = true
            )
            .clip(MaterialTheme.shapes.extraSmall),
        model = imageModel.ifEmpty { R.drawable.sample1 },
        contentDescription = null,
        contentScale = ContentScale.Crop,
    )
}
