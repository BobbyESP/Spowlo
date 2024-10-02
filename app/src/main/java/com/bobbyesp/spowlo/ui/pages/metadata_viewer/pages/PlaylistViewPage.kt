package com.bobbyesp.spowlo.ui.pages.metadata_viewer.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.fromHtml
import androidx.compose.ui.unit.dp
import com.adamratzman.spotify.models.Playlist
import com.bobbyesp.spowlo.App
import com.bobbyesp.spowlo.R
import com.bobbyesp.spowlo.ui.common.AsyncImageImpl
import com.bobbyesp.spowlo.ui.components.HorizontalDivider
import com.bobbyesp.spowlo.ui.components.songs.metadata_viewer.TrackComponent
import com.bobbyesp.spowlo.ui.components.text.MarqueeText
import com.bobbyesp.spowlo.ui.pages.metadata_viewer.binders.dataStringToString

@Composable
fun PlaylistViewPage(
    data: Playlist,
    modifier: Modifier,
    trackDownloadCallback: (String, String) -> Unit
) {
    val localConfig = LocalConfiguration.current

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 8.dp, horizontal = 12.dp),
    ) {
        item {
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
        }
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
                    .padding(top = 6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                SelectionContainer {
                    MarqueeText(
                        text = data.name,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.headlineMedium
                    )
                }
                SelectionContainer {
                    Text(
                        text = data.owner.displayName ?: data.owner.id,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.alpha(alpha = 0.8f)
                    )
                }
                SelectionContainer {
                    Text(
                        text = dataStringToString(
                            data = data.type,
                            additional = data.followers.total.toString() + " " + App.context.getString(
                                R.string.followers
                            )
                                .lowercase()
                        ),
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.alpha(alpha = 0.8f)
                    )
                }
                SelectionContainer {
                    val annotatedString = AnnotatedString.fromHtml(
                        data.description ?: "",
                        linkStyles = TextLinkStyles(
                            style = SpanStyle(
                                color = MaterialTheme.colorScheme.primary,
                            )
                        )
                    )
                    Text(
                        text = annotatedString,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.alpha(alpha = 0.8f)
                    )
                }
            }
        }
        if (data.externalUrls.spotify != null) {
            item {
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
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp))
            }
        }
        if (data.tracks.size > 0) {
            items(data.tracks.items.size) { index ->
                val track = data.tracks.items[index]
                val actualTrack = track.track?.asTrack
                val taskName = StringBuilder().append(actualTrack?.name).append(" - ")
                    .append(actualTrack?.artists?.joinToString(", ") { it.name }).toString()
                TrackComponent(
                    contentModifier = Modifier,
                    songName = actualTrack?.name ?: App.context.getString(R.string.unknown),
                    artists = actualTrack?.artists?.joinToString(", ") { it.name } ?: "",
                    spotifyUrl = actualTrack?.externalUrls?.spotify ?: "",
                    isExplicit = actualTrack?.explicit ?: false,
                    isPlaylist = true,
                    imageUrl = actualTrack?.album?.images?.getOrNull(0)?.url ?: "",
                    onClick = {
                        if (actualTrack != null) {
                            trackDownloadCallback(
                                actualTrack.externalUrls.spotify!!,
                                taskName
                            )
                        }
                    }
                )
            }
        }

        item {
            androidx.compose.material3.HorizontalDivider()
            Text(
                text = stringResource(id = R.string.uncomplete_playlist),
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(12.dp)
            )
        }
    }
}