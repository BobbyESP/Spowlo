package com.bobbyesp.spowlo.ui.components.cards.songs.horizontal

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.bobbyesp.spowlo.features.lyrics_downloader.data.local.model.Song
import com.bobbyesp.spowlo.ui.components.images.AsyncImageImpl
import com.bobbyesp.spowlo.ui.components.text.MarqueeText

@Composable
fun HorizontalSongCard(
    modifier: Modifier = Modifier,
    song: Song,
    onClick: () -> Unit = {},
) {
    Box(modifier) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(),
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
                            .size(84.dp)
                            .aspectRatio(1f, matchHeightConstraintsFirst = true)
                            .clip(MaterialTheme.shapes.small),
                        model = song.albumArtPath.toString(),
                        contentDescription = "Song cover",
                        contentScale = ContentScale.Crop,
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.Start, modifier = Modifier.padding(8.dp).weight(1f)
                        ) {
                            MarqueeText(
                                text = song.title,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold
                            )
                            MarqueeText(
                                text = song.artist, style = MaterialTheme.typography.bodyMedium.copy(
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HorizontalSongCard(
    modifier: Modifier = Modifier,
    song: Song
) {
    Box(modifier) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(),
            shape = MaterialTheme.shapes.small,
        ) {
            Column(Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically, //This makes all go to the center
                ) {
                    AsyncImageImpl(
                        modifier = Modifier
                            .size(84.dp)
                            .aspectRatio(1f, matchHeightConstraintsFirst = true)
                            .clip(MaterialTheme.shapes.small),
                        model = song.albumArtPath.toString(),
                        contentDescription = "Song cover",
                        contentScale = ContentScale.Crop,
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.Start, modifier = Modifier.padding(8.dp).weight(1f)
                        ) {
                            MarqueeText(
                                text = song.title,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold
                            )
                            MarqueeText(
                                text = song.artist, style = MaterialTheme.typography.bodyMedium.copy(
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}