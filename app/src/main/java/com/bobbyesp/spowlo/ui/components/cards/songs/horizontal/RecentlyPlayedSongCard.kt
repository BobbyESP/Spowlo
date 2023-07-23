package com.bobbyesp.spowlo.ui.components.cards.songs.horizontal

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImagePainter
import com.adamratzman.spotify.models.PlayHistory
import com.bobbyesp.spowlo.R
import com.bobbyesp.spowlo.ui.components.images.AsyncImageImpl
import com.bobbyesp.spowlo.ui.components.images.PlaceholderCreator
import com.bobbyesp.spowlo.ui.components.text.MarqueeText
import com.bobbyesp.spowlo.ui.ext.calculateTimeDifference
import com.bobbyesp.spowlo.ui.ext.secondOrNull
import com.bobbyesp.spowlo.utils.localAsset

@Composable
fun RecentlyPlayedSongCard(
    modifier: Modifier = Modifier,
    showSpotifyLogo: Boolean = true,
    playHistoryItem: PlayHistory,
    onClick: () -> Unit = {}
) {
    val track = playHistoryItem.track
    val albumArtPath = playHistoryItem.track.album.images.secondOrNull()?.url
    var showArtwork by remember { mutableStateOf(true) }

    Box(modifier) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            onClick = onClick,
            shape = MaterialTheme.shapes.small,
        ) {
            Column(Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box {
                        if (albumArtPath != null) {
                            AsyncImageImpl(
                                modifier = Modifier
                                    .size(60.dp)
                                    .aspectRatio(1f, matchHeightConstraintsFirst = true)
                                    .clip(MaterialTheme.shapes.extraSmall),
                                model = albumArtPath,
                                onState = { state ->
                                    //if it was successful, don't show the placeholder, else show it
                                    showArtwork =
                                        state !is AsyncImagePainter.State.Error && state !is AsyncImagePainter.State.Empty
                                },
                                contentDescription = "Song cover",
                                contentScale = ContentScale.Fit,
                                isPreview = false
                            )
                        } else {
                            PlaceholderCreator(
                                modifier = Modifier
                                    .size(84.dp)
                                    .aspectRatio(1f, matchHeightConstraintsFirst = true)
                                    .clip(MaterialTheme.shapes.small),
                                icon = Icons.Default.MusicNote,
                                colorful = false,
                                contentDescription = "Song cover"
                            )
                        }
                        if (showSpotifyLogo) {
                            Column(
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .padding(4.dp)
                            ) {
                                Icon(
                                    imageVector = localAsset(id = R.drawable.spotify_logo),
                                    contentDescription = "Spotify logo",
                                    modifier = Modifier.size(12.dp),
                                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )
                            }
                        }
                    }
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.Start,
                            modifier = Modifier
                                .padding(8.dp)
                                .padding(start = 4.dp)
                                .weight(1f)
                        ) {
                            MarqueeText(
                                text = track.name,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold
                            )
                            MarqueeText(
                                text = track.artists.joinToString(", ") { it.name },
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )
                            )
                        }
                    }
                    Column(
                        modifier = Modifier.padding(end = 4.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = "History icon",
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        )
                        Text(
                            text = playHistoryItem.playedAt.calculateTimeDifference(),
                            fontWeight = FontWeight.SemiBold,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        )
                    }
                }
            }
        }
    }
}