package com.bobbyesp.spowlo.ui.components.cards.songs

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.adamratzman.spotify.models.Track
import com.bobbyesp.spowlo.R
import com.bobbyesp.spowlo.ui.components.images.AsyncImageImpl
import com.bobbyesp.spowlo.ui.components.images.PlaceholderCreator
import com.bobbyesp.spowlo.ui.components.text.MarqueeText
import com.bobbyesp.spowlo.ui.ext.secondOrNull
import com.bobbyesp.spowlo.ui.theme.SpowloTheme
import com.bobbyesp.spowlo.utils.localAsset

@Composable
fun SpotifySongCard(
    modifier: Modifier = Modifier,
    track: Track,
    onClick: () -> Unit,
    showSpotifyLogo: Boolean = true
) {
    val albumArtPath = track.album.images.secondOrNull()?.url

    Box(
        modifier
            .clip(MaterialTheme.shapes.extraSmall)
            .clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
        ) {
            Box(
                modifier = Modifier
                    .clip(MaterialTheme.shapes.extraSmall)
                    .fillMaxWidth()
                    .size(90.dp)
                    .aspectRatio(1f),
            ) {
                if (albumArtPath != null) {
                    AsyncImageImpl(
                        modifier = Modifier
                            .fillMaxSize()
                            .align(Alignment.Center),
                        model = albumArtPath,
                        contentDescription = "Album Artwork"
                    )
                } else {
                    PlaceholderCreator(
                        modifier = Modifier
                            .fillMaxSize()
                            .align(Alignment.Center),
                        icon = Icons.Default.MusicNote,
                        colorful = true
                    )
                }
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
            Column(
                horizontalAlignment = Alignment.Start, modifier = Modifier.padding(8.dp)
            ) {
                MarqueeText(
                    text = track.name,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
                MarqueeText(
                    text = track.artists.joinToString(", ") { it.name },
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                )
            }
        }
    }
}

@Composable
fun SmallSpotifySongCard(
    modifier: Modifier = Modifier,
    track: Track,
    onClick: () -> Unit,
    number: Int? = null,
    showSpotifyLogo: Boolean = true
) {
    val albumArtPath = track.album.images.secondOrNull()?.url

    Box(
        modifier
            .clip(MaterialTheme.shapes.extraSmall)
            .clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
        ) {
            Box(
                modifier = Modifier
                    .clip(MaterialTheme.shapes.extraSmall)
                    .fillMaxWidth()
                    .size(100.dp)
                    .aspectRatio(1f),
            ) {
                if (albumArtPath != null) {
                    AsyncImageImpl(
                        modifier = Modifier
                            .fillMaxSize()
                            .align(Alignment.Center),
                        model = albumArtPath,
                        contentDescription = "Album Artwork"
                    )
                } else {
                    PlaceholderCreator(
                        modifier = Modifier
                            .fillMaxSize()
                            .align(Alignment.Center),
                        icon = Icons.Default.MusicNote,
                        colorful = true
                    )
                }
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
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (number != null) {
                    Text(
                        text = "$number.",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        ),
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(2.dp)
                    )
                }
                Column(
                    horizontalAlignment = Alignment.Start, modifier = Modifier.padding(8.dp)
                ) {
                    MarqueeText(
                        text = track.name,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                    MarqueeText(
                        text = track.artists.joinToString(", ") { it.name },
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    )
                }
            }
        }
    }
}

@Preview
@Preview(uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun Test() {
    SpowloTheme {
    }
}