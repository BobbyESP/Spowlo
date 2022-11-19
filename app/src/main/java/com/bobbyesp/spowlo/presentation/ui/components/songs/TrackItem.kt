package com.bobbyesp.spowlo.presentation.ui.components.songs

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.adamratzman.spotify.models.Track

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrackItem(
    modifier: Modifier = Modifier,
    track: Track,
    onClick: (Track) -> Unit
) {
    Surface(
        modifier = modifier.clickable(onClick = { onClick(track) })
    ) {
        Row(
            modifier = Modifier.padding(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            AsyncImage(
                modifier = Modifier
                    .size(100.dp)
                    .padding(start = 10.dp, end = 15.dp, top = 5.dp, bottom = 5.dp)
                    .clip(MaterialTheme.shapes.medium),
                contentScale = ContentScale.Fit,
                model = ImageRequest.Builder(LocalContext.current)
                    .data(track.album.images.firstOrNull()?.url)
                    .crossfade(true)
                    .build(),
                contentDescription = "Album cover"
            )
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = track.name,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = track.artists.joinToString(", ") { it.name },
                    style = MaterialTheme.typography.bodySmall
                )

            }
        }

    }
}
