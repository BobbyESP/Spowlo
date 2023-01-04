package com.bobbyesp.spowlo.presentation.ui.components

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
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Surface
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
import com.bobbyesp.spowlo.presentation.ui.common.AsyncImageImpl
import com.bobbyesp.spowlo.presentation.ui.components.songs.ExplicitIcon
import com.bobbyesp.spowlo.presentation.ui.components.songs.LyricsIcon
import com.bobbyesp.spowlo.presentation.ui.components.text.MarqueeText

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SongCard(
    song: Song,
    onClick: () -> Unit = {},
    progress: Float = 69f,
    isPreview: Boolean = false,
    isExplicit: Boolean = true,
    isLyrics: Boolean = false,
) {
    Box() {
        ElevatedCard(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface),
            onClick = { onClick() },
            shape = MaterialTheme.shapes.small,
        ) {
            Column(Modifier.fillMaxWidth()) {
                Box(Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
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
                        Column(
                            modifier = Modifier.padding(6.dp),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.Start
                        ) {
                            Row(
                                Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                MarqueeText(
                                    text = song.name, color = MaterialTheme.colorScheme.onSurface,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                ExplicitIcon(
                                    visible = isExplicit,
                                    modifier = Modifier.padding(start = 4.dp, end = 4.dp)
                                )
                                LyricsIcon(
                                    visible = isLyrics,
                                    modifier = Modifier.padding(start = 4.dp, end = 4.dp)
                                )
                            }

                            Spacer(Modifier.height(8.dp))
                            MarqueeText(
                                text = song.artist,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                fontSize = 12.sp
                            )
                        }
                    }
                    val progressAnimationValue by animateFloatAsState(
                        targetValue = progress,
                        animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec
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
                            progress = progressAnimationValue / 100f,
                            color = MaterialTheme.colorScheme.primary,
                        )
                }
            }
        }
    }
}

@Preview()
@Composable
fun showSongCard() {
    Surface {
        SongCard(
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

@Preview(name = "Dark Mode",
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
fun showSongCardNight() {
    Surface {
        SongCard(
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