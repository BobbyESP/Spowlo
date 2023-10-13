package com.bobbyesp.spowlo.ui.components.songs

import android.content.res.Configuration
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
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
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bobbyesp.library.dto.Song
import com.bobbyesp.spowlo.ui.common.AsyncImageImpl
import com.bobbyesp.spowlo.ui.components.MarqueeText
import com.bobbyesp.spowlo.utils.GeneralTextUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SongCard(
    modifier: Modifier = Modifier,
    song: Song,
    onClick: () -> Unit = {},
    progress: Float = 0.69f,
    isPreview: Boolean = false,
    isExplicit: Boolean = true,
    isLyrics: Boolean = false,
) {
    Box(modifier) {
        ElevatedCard(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface),
            onClick = { onClick() },
            shape = MaterialTheme.shapes.small,
        ) {
            Column(Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically, //This makes all go to the center
                ) {
                    AsyncImageImpl(
                        modifier = Modifier
                            .padding(16.dp)
                            .size(84.dp)
                            .aspectRatio(1f, matchHeightConstraintsFirst = true)
                            .clip(MaterialTheme.shapes.small),
                        model = song.cover_url,
                        contentDescription = "Song cover",
                        contentScale = ContentScale.Crop,
                        isPreview = isPreview
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
                                    text = song.name, color = MaterialTheme.colorScheme.onSurface,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    basicGradientColor = MaterialTheme.colorScheme.surface.copy(
                                        alpha = 0.8f
                                    ),
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                ExplicitIcon(
                                    visible = isExplicit
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                LyricsIcon(
                                    visible = false //isLyrics
                                )
                            }
                            Spacer(Modifier.height(8.dp))
                            MarqueeText(
                                text = song.artist,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                fontSize = 12.sp,
                                basicGradientColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
                            )
                        }
                        Column(
                            modifier = Modifier
                                .padding(16.dp),
                            horizontalAlignment = Alignment.End
                        ) {
                            Surface(
                                modifier = Modifier,
                                color = MaterialTheme.colorScheme.secondaryContainer,
                                shape = MaterialTheme.shapes.extraSmall
                            ) {
                                Row(
                                    modifier = Modifier,
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center,
                                )
                                {
                                    Text(
                                        text = GeneralTextUtils.convertDuration(song.duration),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(6.dp, 4.dp, 6.dp, 4.dp)
                                    )
                                }
                            }
                        }
                    }
                }
                Box(Modifier.fillMaxWidth()) {
                    val progressAnimationValue by animateFloatAsState(
                        targetValue = progress,
                        animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec, label = ""
                    )
                    if (progress < 0f)
                        LinearProgressIndicator(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(4.dp)
                                .align(Alignment.BottomCenter),
                            color = MaterialTheme.colorScheme.primary,
                        )
                    else
                        LinearProgressIndicator(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(4.dp)
                                .align(Alignment.BottomCenter),
                            progress = progressAnimationValue,
                            color = MaterialTheme.colorScheme.primary,
                        )
                }
            }
        }
    }
}

@Preview()
@Composable
fun ShowSongCard() {
    Surface {
        SongCard(
            song =
            Song(
                "Save Your Tears",
                listOf("The Weekend"),
                "The Weeknd",
                "url",
                "",
                emptyList(),
                0,
                1,
                179.8,
                2022,
                "",
                0,
                0,
                "",
                true,
                "Quevedo",
                "url",
                "url",
                "https://i.scdn.co/image/ab67616d0000b2730da5b28d9dfe894de5da63ff",
                "",
                "",
                null,
                null,
                null,
            ),
            isPreview = true
        )
    }
}

@Preview(
    name = "Dark Mode",
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
fun ShowSongCardNight() {
    Surface {
        SongCard(
            song =
            Song(
                "mariposas",
                listOf("sangiovanni"),
                "sangiovanni",
                "url",
                "",
                emptyList(),
                0,
                1,
                17.8,
                2022,
                "",
                0,
                0,
                "",
                false,
                "Quevedo",
                "url",
                "url",
                "https://i.scdn.co/image/ab67616d0000b2730da5b28d9dfe894de5da63ff",
                "",
                "",
                null,
                null,
                null,
            ),
            isPreview = true
        )
    }
}