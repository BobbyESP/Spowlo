package com.bobbyesp.spowlo.ui.components.cards.horizontal

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.adamratzman.spotify.models.Track
import com.bobbyesp.spowlo.R
import com.bobbyesp.spowlo.ui.components.images.AsyncImageImpl
import com.bobbyesp.spowlo.ui.components.text.MarqueeText
import com.bobbyesp.spowlo.ui.localAsset

@Composable
fun SpotifyHorizontalSongCard(
    modifier: Modifier = Modifier,
    track: Track,
    showSpotifyLogo: Boolean = true,
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
                    Box {
                        AsyncImageImpl(
                            modifier = Modifier
                                .size(84.dp)
                                .aspectRatio(1f, matchHeightConstraintsFirst = true)
                                .clip(MaterialTheme.shapes.small),
                            model = track.album.images[0].url,
                            contentDescription = "Song cover",
                            contentScale = ContentScale.Crop,
                        )
                        if (showSpotifyLogo) Column(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(8.dp),
                        ) {
                            Icon(
                                imageVector = localAsset(id = R.drawable.spotify_logo),
                                contentDescription = "Spotify logo",
                                modifier = Modifier.size(24.dp),
                                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        }
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.Start, modifier = Modifier.padding(8.dp).weight(1f)
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
                }
            }
        }
    }
}

@Composable
fun SpotifyHorizontalSongCard(
    modifier: Modifier = Modifier,
    showSpotifyLogo: Boolean = true,
    track: Track,
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
                    Box {
                        AsyncImageImpl(
                            modifier = Modifier
                                .size(84.dp)
                                .aspectRatio(1f, matchHeightConstraintsFirst = true)
                                .clip(MaterialTheme.shapes.small),
                            model = track.album.images[0].url,
                            contentDescription = "Song cover",
                            contentScale = ContentScale.Crop,
                        )
                        if (showSpotifyLogo) Column(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(8.dp),
                        ) {
                            Icon(
                                imageVector = localAsset(id = R.drawable.spotify_logo),
                                contentDescription = "Spotify logo",
                                modifier = Modifier.size(24.dp),
                                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        }
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.Start, modifier = Modifier.padding(8.dp).weight(1f)
                        ) {
                            MarqueeText(
                                text = track.name,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold
                            )
                            MarqueeText(
                                text = track.artists.joinToString(", ") { it.name }, style = MaterialTheme.typography.bodyMedium.copy(
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