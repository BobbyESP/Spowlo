@file:OptIn(ExperimentalFoundationApi::class)

package com.bobbyesp.spowlo.ui.components.history

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bobbyesp.spowlo.R
import com.bobbyesp.spowlo.ui.common.AsyncImageImpl
import com.bobbyesp.spowlo.ui.common.LocalWindowWidthState
import com.bobbyesp.spowlo.ui.components.MarqueeText
import com.bobbyesp.spowlo.ui.components.songs.CustomTag
import com.bobbyesp.spowlo.ui.components.songs.ExplicitIcon
import com.bobbyesp.spowlo.ui.components.songs.LyricsIcon
import com.bobbyesp.spowlo.ui.components.songs.MiniMetadataInfoComponent
import com.bobbyesp.spowlo.utils.GeneralTextUtils
import com.bobbyesp.spowlo.utils.toFileSizeText

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun HistoryMediaItem(
    modifier: Modifier = Modifier,
    songName: String = "",
    author: String = "",
    artworkUrl: String = "",
    songPath: String = "",
    songSpotifyUrl: String = "",
    songFileSize: Long = 0L,
    songDuration: String = "03:24",
    fileType: String = "OPUS",
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
                .padding(2.dp)
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
            Column(Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically, //This makes all go to the center
                ) {
                    AsyncImageImpl(
                        modifier = Modifier
                            .padding(horizontal = 12.dp)
                            .size(86.dp)
                            .aspectRatio(1f, matchHeightConstraintsFirst = true)
                            .clip(MaterialTheme.shapes.small),
                        model = artworkUrl,
                        contentDescription = "Song cover",
                        contentScale = ContentScale.Crop,
                        isPreview = false
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(6.dp)
                                .weight(1f), //Weight is to make the time not go away from the screen
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.Start
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                MarqueeText(
                                    text = songName, color = MaterialTheme.colorScheme.onSurface,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    basicGradientColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
                                )
                            }
                            Spacer(Modifier.height(8.dp))
                            MarqueeText(
                                text = author,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                fontSize = 12.sp,
                                basicGradientColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
                            )
                            if(!isFileAvailable) {
                                Text(
                                    modifier = Modifier.padding(top = 3.dp),
                                    text = stringResource(
                                        R.string.unavailable
                                    ),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.error,
                                    maxLines = 1,
                                )
                            }
                        }
                        Column(
                            modifier = Modifier
                                .padding(16.dp),
                            horizontalAlignment = Alignment.End
                        ) {
                            CustomTag(text = songDuration)
                            Spacer(Modifier.height(6.dp))
                            CustomTag(text = fileType)
                            Spacer(Modifier.height(6.dp))
                            CustomTag(text = fileSizeText)
                        }
                    }
                }
            }

            /*ArtworkImage(
                modifier = Modifier.weight(1f - imageWeight),
                imageModel = artworkUrl
            )
            Column(
                modifier = Modifier
                    .weight(1f - imageWeight)
                    .padding(horizontal = 12.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.Top
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .align(Alignment.Start),
                ) {
                    MarqueeText(
                        text = songName,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleLarge
                    )
                    if (author != "") {
                        Text(
                            modifier = Modifier.padding(top = 3.dp),
                            text = author,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .align(Alignment.End),
                ){
                    Column {
                        MiniMetadataInfoComponent(text = songDuration)
                        Spacer(modifier = Modifier.height(4.dp))
                        if (isFileAvailable) {
                            MiniMetadataInfoComponent(
                                text = fileSizeText
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        MiniMetadataInfoComponent(
                            text = fileType
                        )
                    }
                }
            }*/
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
