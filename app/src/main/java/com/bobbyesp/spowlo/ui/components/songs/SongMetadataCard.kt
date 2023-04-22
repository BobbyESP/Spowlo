package com.bobbyesp.spowlo.ui.components.songs

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bobbyesp.library.dto.Song
import com.bobbyesp.spowlo.R
import com.bobbyesp.spowlo.ui.common.AsyncImageImpl
import com.bobbyesp.spowlo.ui.components.MarqueeText
import com.bobbyesp.spowlo.ui.theme.harmonizeWithPrimary
import com.bobbyesp.spowlo.utils.ChromeCustomTabsUtil
import com.bobbyesp.spowlo.utils.GeneralTextUtils

@Composable
fun SongMetadataCard(
    song: Song
) {
    val isrc = song.isrc ?: "Unknown"
    val genres = song.genres ?: listOf("Unknown")
    val disc_number = song.disc_number ?: 0
    val disc_count = song.disc_count ?: 0
    val track_number = song.track_number ?: 0
    val publisher = song.publisher ?: "Unknown"
    val copyrightText = song.copyright_text ?: "Unknown"

    val metadataMap = mapOf(
        stringResource(id = R.string.song_name) to song.name,
        stringResource(id = R.string.main_artist) to song.artist,
        stringResource(id = R.string.song_artists) to song.artists.toString(),
        stringResource(id = R.string.song_album) to song.album_name,
        stringResource(id = R.string.song_album_artist) to song.album_artist,
        stringResource(id = R.string.song_genres) to genres.toString(),
        stringResource(id = R.string.song_disc_number) to disc_number.toString(),
        stringResource(id = R.string.song_disc_count) to disc_count.toString(),
        stringResource(id = R.string.song_duration) to song.duration.toString(),
        stringResource(id = R.string.song_year) to song.year.toString(),
        stringResource(id = R.string.song_date) to song.date,
        stringResource(id = R.string.song_track_number) to track_number.toString(),
        stringResource(id = R.string.song_spotify_id) to song.song_id,
        stringResource(id = R.string.song_is_explicit) to song.explicit.toString(),
        stringResource(id = R.string.song_publisher) to publisher,
        stringResource(id = R.string.song_isrc) to isrc,
        stringResource(id = R.string.song_url) to song.url,
        stringResource(id = R.string.song_cover_url) to song.cover_url,
        stringResource(id = R.string.song_copyright_text) to copyrightText,
    )

    val metadataList = metadataMap.toList()
    val firstNineElements = metadataList.subList(0, 10)
    val lastElements = metadataList.subList(10, metadataList.size)

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth(),
        shape = MaterialTheme.shapes.small
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImageImpl(
                modifier = Modifier
                    .size(64.dp)
                    .aspectRatio(
                        1f,
                        matchHeightConstraintsFirst = true
                    )
                    .clip(MaterialTheme.shapes.small),
                model = song.cover_url,
                contentDescription = "Song cover",
                contentScale = ContentScale.Crop,
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 8.dp)
                    .weight(1f)
            ) {
                MarqueeText(
                    text = song.name,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleLarge
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = song.artist,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.alpha(alpha = 0.8f)
                )

            }
            FilledTonalButton(
                modifier = Modifier.padding(start = 8.dp),
                onClick = { ChromeCustomTabsUtil.openUrl(song.url) },
                colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = Color(red = 30, green = 215, blue = 96).harmonizeWithPrimary(),
                ),
            )
            {
                Icon(
                    modifier = Modifier.size(24.dp),
                    imageVector = ImageVector.vectorResource(id = R.drawable.spotify_logo),
                    contentDescription = null
                )
            }

        }
        Row(Modifier.fillMaxWidth()) {
            Column(Modifier.fillMaxWidth(0.5f)) {
                firstNineElements.forEach { (key, value) ->
                    MetadataTag(key, value)
                }
            }
            Column(Modifier.fillMaxWidth()) {
                lastElements.forEach { (key, value) ->
                    MetadataTag(key, value)
                }
            }
        }
        /*
        LazyVerticalGrid(columns = GridCells.Adaptive(150.dp),
            content = {
                metadataMap.forEach {
                    item {
                        MetadataTag(typeOfMetadata = it.key, metadata = it.value)
                    }
                }
            }, userScrollEnabled = false,
        )*/
    }
}

@Composable
fun PlaylistHeaderItem(
    playlist: Song,
    modifier: Modifier = Modifier
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth(),
        shape = MaterialTheme.shapes.small,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            playlist.song_list?.cover_url?.let {
                AsyncImageImpl(
                    modifier = Modifier
                        .padding(16.dp)
                        .size(84.dp)
                        .aspectRatio(
                            1f,
                            matchHeightConstraintsFirst = true
                        )
                        .clip(MaterialTheme.shapes.small),
                    model = it,
                    contentDescription = stringResource(id = R.string.playlist_cover),
                    contentScale = ContentScale.Crop,
                )
            }
            Column() {
                Text(
                    text = playlist.song_list?.name ?: stringResource(id = R.string.unknown),
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleLarge
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = playlist.song_list?.author_name
                        ?: stringResource(id = R.string.unknown),
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.alpha(alpha = 0.8f)
                )
            }
        }
    }
}

@Composable
fun MetadataTag(
    typeOfMetadata: String,
    metadata: String = "Unknown",
) {
    val clipboardManager = LocalClipboardManager.current

    Column(
        modifier = Modifier
            .padding(8.dp)
            .clip(MaterialTheme.shapes.small)
            .alpha(alpha = 0.8f)
    ) {
        Text(
            text = typeOfMetadata,
            modifier = Modifier.alpha(alpha = 0.8f),
            style = MaterialTheme.typography.labelSmall,
            textAlign = TextAlign.Start
        )
        Text(
            modifier = Modifier
                .clickable { GeneralTextUtils.copyToClipboardAndNotify(metadata) }
                .fillMaxWidth(),
            text = metadata,
            style = MaterialTheme.typography.titleLarge.copy(fontSize = 16.sp),
            textAlign = TextAlign.Start
        )
    }
}

@Preview
@Composable
fun SongMetadataCardPreview() {
    SongMetadataCard(
        song = Song(
            name = "Song name",
            artist = "Artist name",
            cover_url = ""
        )
    )
}