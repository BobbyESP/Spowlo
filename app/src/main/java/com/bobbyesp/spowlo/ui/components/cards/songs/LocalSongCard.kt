package com.bobbyesp.spowlo.ui.components.cards.songs

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.net.Uri
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImagePainter
import com.bobbyesp.spowlo.features.lyrics_downloader.data.local.model.Song
import com.bobbyesp.spowlo.ui.components.images.AsyncImageImpl
import com.bobbyesp.spowlo.ui.components.images.PlaceholderCreator
import com.bobbyesp.spowlo.ui.components.text.MarqueeText
import com.bobbyesp.spowlo.ui.theme.SpowloTheme

@Composable
fun LocalSongCard(
    modifier: Modifier = Modifier,
    song: Song,
    onClick: () -> Unit
) {
    var showArtwork by remember { mutableStateOf(true) }

    Surface(
        modifier = modifier
            .clip(MaterialTheme.shapes.small),
        onClick = onClick
    ) {
        Column {
            if (song.albumArtPath != null && showArtwork) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                ) {
                    AsyncImageImpl(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(MaterialTheme.shapes.small),
                        model = song.albumArtPath,
                        onState = { state ->
                            //if it was successful, don't show the placeholder, else show it
                            showArtwork = state !is AsyncImagePainter.State.Error && state !is AsyncImagePainter.State.Empty
                        },
                        contentDescription = "Song cover",
                        contentScale = ContentScale.Fit,
                        isPreview = false
                    )
                }
            } else {
                PlaceholderCreator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                        .clip(MaterialTheme.shapes.small),
                    icon = Icons.Default.MusicNote,
                    colorful = false,
                    contentDescription = "Song cover"
                )
            }
            Column(
                horizontalAlignment = Alignment.Start, modifier = Modifier.padding(8.dp)
            ) {
                MarqueeText(
                    text = song.title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
                MarqueeText(
                    text = song.artist,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    ),
                    fontSize = 12.sp
                )
            }
        }
    }
}


@Preview
@Preview(uiMode = UI_MODE_NIGHT_YES)
@Composable
fun LocalSongCardPreview() {
    SpowloTheme {
        LocalSongCard(song = Song(
            id = 1,
            title = "Bones",
            artist = "Imagine Dragons",
            album = "Mercury - Acts 1 & 2",
            albumArtPath = Uri.parse("E:\\Programacion\\Android\\Spowlo\\app\\src\\main\\res\\drawable\\bones_imaginedragons_testimage.webp"),
            duration = 100.0,
            path = "path"
        ), onClick = {})
    }
}