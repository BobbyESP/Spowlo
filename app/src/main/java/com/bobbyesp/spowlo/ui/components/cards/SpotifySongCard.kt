package com.bobbyesp.spowlo.ui.components.cards

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
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
fun SpotifySongCard(
    modifier: Modifier = Modifier,
    track: Track,
    onClick: () -> Unit,
    showSpotifyLogo: Boolean = true
) {
    Surface(modifier = modifier
        .clip(MaterialTheme.shapes.small)
        .clickable {
            onClick()
        }) {
        Column(modifier = Modifier.fillMaxSize()) {
            track.album.images.firstOrNull()!!.url.let {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                ) {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        AsyncImageImpl(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(MaterialTheme.shapes.small)
                                .align(Alignment.Center),
                            model = it,
                            contentDescription = "Song cover",
                            contentScale = ContentScale.Fit,
                            isPreview = false
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
                                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                            )
                        }
                    }
                }
            }
            Column(
                horizontalAlignment = Alignment.Start, modifier = Modifier.padding(8.dp)
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