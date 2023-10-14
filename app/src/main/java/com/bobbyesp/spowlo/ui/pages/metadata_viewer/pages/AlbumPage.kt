package com.bobbyesp.spowlo.ui.pages.metadata_viewer.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.adamratzman.spotify.models.Album
import com.bobbyesp.spowlo.R
import com.bobbyesp.spowlo.ui.common.AsyncImageImpl
import com.bobbyesp.spowlo.ui.components.HorizontalDivider
import com.bobbyesp.spowlo.ui.components.text.MarqueeText
import com.bobbyesp.spowlo.ui.components.songs.metadata_viewer.TrackComponent
import com.bobbyesp.spowlo.ui.pages.metadata_viewer.binders.dataStringToString

@Composable
fun AlbumPage(
    data: Album,
    modifier: Modifier,
    trackDownloadCallback: (String, String) -> Unit
) {
    val localConfig = LocalConfiguration.current

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top
    ) {
        Box(
            modifier = Modifier
                .clip(MaterialTheme.shapes.extraSmall)
                .fillMaxWidth()
                .padding(bottom = 6.dp),
            contentAlignment = Alignment.Center
        ) {
            //calculate the image size based on the screen size and the aspect ratio as 1:1 (square) based on the height
            val size = (localConfig.screenHeightDp / 3)
            AsyncImageImpl(
                modifier = Modifier
                    .size(size.dp)
                    .aspectRatio(
                        1f, matchHeightConstraintsFirst = true
                    )
                    .clip(MaterialTheme.shapes.small),
                model = data.images[0].url,
                contentDescription = stringResource(id = R.string.track_artwork),
                contentScale = ContentScale.Crop,
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, top = 8.dp)
        ) {
            SelectionContainer {
                MarqueeText(
                    text = data.name,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.headlineMedium
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            SelectionContainer {
                Text(
                    text = data.artists.joinToString(", ") { it.name },
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.alpha(alpha = 0.8f)
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            SelectionContainer {
                Text(
                    text = dataStringToString(
                        data = data.type, additional = data.releaseDate.year.toString(),
                    ),
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.alpha(alpha = 0.8f)
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            if (data.externalUrls.spotify != null) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FilledTonalIconButton(
                        onClick = {
                            trackDownloadCallback(data.externalUrls.spotify!!, data.name)
                        },
                        modifier = Modifier.size(48.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Download,
                            contentDescription = "Download full playlist icon",
                            modifier = Modifier
                                .weight(1f)
                                .padding(14.dp)
                        )
                    }

                }
            }
            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp))
            if (data.tracks.size > 0) {
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    data.tracks.items.forEach { track ->
                        val taskName = StringBuilder().append(track.name).append(" - ").append(
                            track?.artists?.joinToString(", ") { it.name }).toString()
                        TrackComponent(
                            contentModifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                            songName = track.name,
                            artists = track.artists.joinToString(", ") { it.name },
                            spotifyUrl = track.externalUrls.spotify ?: "",
                            isExplicit = track.explicit,
                            onClick = {
                                trackDownloadCallback(
                                    track.externalUrls.spotify!!,
                                    taskName
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}