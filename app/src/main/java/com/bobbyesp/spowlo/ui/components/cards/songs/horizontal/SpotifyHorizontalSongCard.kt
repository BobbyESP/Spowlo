import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
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
import com.adamratzman.spotify.models.Track
import com.bobbyesp.spowlo.R
import com.bobbyesp.spowlo.features.lyrics_downloader.domain.model.Song
import com.bobbyesp.spowlo.ui.components.images.AsyncImageImpl
import com.bobbyesp.spowlo.ui.components.images.PlaceholderCreator
import com.bobbyesp.spowlo.ui.components.others.PlayingIndicator
import com.bobbyesp.spowlo.ui.components.text.MarqueeText
import com.bobbyesp.spowlo.ui.ext.secondOrNull
import com.bobbyesp.spowlo.utils.localAsset

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SpotifyHorizontalSongCard(
    modifier: Modifier = Modifier,
    showSpotifyLogo: Boolean = true,
    track: Track? = null,
    song: Song? = null,
    listIndex: Int? = null,
    isPlaying: Boolean = false,
    onLongClick: () -> Unit = {},
    onClick: () -> Unit = {}
) {
    val albumArtPath = track?.album?.images?.get(0)?.url ?: song?.albumArtPath
    var showArtwork by remember { mutableStateOf(true) }

    Box(modifier) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(MaterialTheme.shapes.small)
                .combinedClickable(
                    onClick = onClick,
                    onLongClick = onLongClick
                ),
        ) {
            Column(Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (listIndex != null) {
                        Text(
                            text = "${listIndex + 1}",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .padding(8.dp)
                                .padding(end = 4.dp)
                        )
                    }
                    Box(contentAlignment = Alignment.CenterStart) {
                        if (albumArtPath != null && showArtwork) {
                            AsyncImageImpl(
                                modifier = Modifier
                                    .size(90.dp)
                                    .aspectRatio(1f, matchHeightConstraintsFirst = true)
                                    .clip(MaterialTheme.shapes.small),
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
                                    .size(90.dp)
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
                                .weight(1f)
                        ) {
                            MarqueeText(
                                text = track?.name ?: song?.title ?: "",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold
                            )
                            MarqueeText(
                                text = track?.artists?.joinToString(", ") { it.name }
                                    ?: song?.artist ?: "",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )
                            )
                        }
                    }
                    AnimatedVisibility(visible = isPlaying) {
                        PlayingIndicator(
                            modifier = Modifier
                                .height(24.dp)
                                .padding(end = 12.dp),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CompactSpotifyHorizontalSongCard(
    modifier: Modifier = Modifier,
    track: Track,
    listIndex: Int? = null,
    onLongClick: () -> Unit = {},
    onClick: () -> Unit = {}
) {
    val albumArtPath = track.album.images.secondOrNull()?.url
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.extraSmall)
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            ),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (listIndex != null) {
                Text(
                    text = "${listIndex + 1}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .padding(8.dp)
                        .padding(end = 4.dp)
                )
            }
            Box(contentAlignment = Alignment.CenterStart) {
                if (albumArtPath != null) {
                    AsyncImageImpl(
                        modifier = Modifier
                            .size(90.dp)
                            .aspectRatio(1f, matchHeightConstraintsFirst = true)
                            .clip(MaterialTheme.shapes.small),
                        model = albumArtPath,
                        contentDescription = "Song cover",
                        contentScale = ContentScale.Fit,
                        isPreview = false
                    )
                } else {
                    PlaceholderCreator(
                        modifier = Modifier
                            .size(90.dp)
                            .aspectRatio(1f, matchHeightConstraintsFirst = true)
                            .clip(MaterialTheme.shapes.small),
                        icon = Icons.Default.MusicNote,
                        colorful = false,
                        contentDescription = "Song cover"
                    )
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
                        .weight(1f),
                    verticalArrangement = Arrangement.Center
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
