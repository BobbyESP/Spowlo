package com.bobbyesp.spowlo.presentation.components.spotify.card

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bobbyesp.spowlo.presentation.components.spotify.image.AsyncImage
import com.bobbyesp.spowlo.presentation.theme.SpowloTheme

@Composable
fun CompactSongCard(
    modifier: Modifier = Modifier,
    artworkUrl: String? = null,
    name: String,
    artists: String,
    listIndex: Int? = null,
    size: CompactCardSize = CompactCardSize.MEDIUM
) {
    Box(
        modifier = modifier
            .clip(MaterialTheme.shapes.medium)
            .size(size.value)
    ) {
        AsyncImage(
            modifier = Modifier.fillMaxSize(),
            imageModel = artworkUrl,
        )

        listIndex?.let {
            Text(
                text = "$it.",
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.8f),
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(8.dp)
                    .align(Alignment.TopEnd)
            )
        }
        Column(
            modifier = Modifier
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            MaterialTheme.colorScheme.scrim
                        ),
                        startY = 0f,
                        endY = 500f
                    ),
                    alpha = 0.6f
                )
                .fillMaxSize()
                .padding(horizontal = 8.dp, vertical = 6.dp),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = name,
                style = MaterialTheme.typography.titleSmall,
                color = Color.White,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1
            )

            if(artists.isNotEmpty()) {
                Text(
                    text = artists,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.6f),
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1
                )
            }
        }
    }
}

@Preview
@Composable
private fun CompactSongCardPrev() {
    SpowloTheme {
        CompactSongCard(
            artworkUrl = "https://i.scdn.co/image/ab67757000003b829579886e94d9940701c3a798",
            name = "Song Name",
            artists = "Artist 1, Artist 2",
            size = CompactCardSize.EXTRA_LARGE,
            listIndex = 1
        )
    }
}