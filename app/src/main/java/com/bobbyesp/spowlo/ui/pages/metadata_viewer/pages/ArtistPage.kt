package com.bobbyesp.spowlo.ui.pages.metadata_viewer.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.adamratzman.spotify.models.Artist
import com.adamratzman.spotify.models.Track
import com.bobbyesp.spowlo.R
import com.bobbyesp.spowlo.features.spotify_api.data.remote.SpotifyApiRequests
import com.bobbyesp.spowlo.ui.common.AsyncImageImpl
import com.bobbyesp.spowlo.ui.components.HorizontalDivider
import com.bobbyesp.spowlo.ui.components.songs.metadata_viewer.TrackComponent
import com.bobbyesp.spowlo.ui.components.text.MarqueeText
import com.bobbyesp.spowlo.ui.theme.harmonizeWithPrimary
import com.bobbyesp.spowlo.utils.ChromeCustomTabsUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext

@Composable
fun ArtistPage(
    data: Artist,
    modifier: Modifier,
    trackDownloadCallback: (String, String) -> Unit
) {
    val localConfig = LocalConfiguration.current
    val topTracks = remember { mutableStateOf<List<Track>?>(null) }

    LaunchedEffect(Unit) {
        // topTracks.value = geArtistTopTracks(data.id)
        val featsAsync = withContext(Dispatchers.IO) {
            async {
                SpotifyApiRequests.providesGetArtistTopTracks(data.id)
            }
        }
        if (topTracks.value == null) {
            topTracks.value = featsAsync.await()
        }
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 8.dp, horizontal = 12.dp),
    ) {
        item {
            data.images.getOrNull(0)?.url?.let { imageUrl ->
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
                            .aspectRatio(1f, matchHeightConstraintsFirst = true)
                            .clip(MaterialTheme.shapes.small),
                        model = imageUrl,
                        contentDescription = stringResource(id = R.string.track_artwork),
                        contentScale = ContentScale.Crop,
                    )
                }
            }
        }

        item {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
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
                        text = "${stringResource(id = R.string.artist_followers)}: ${data.followers.total}",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.alpha(alpha = 0.8f)
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                SelectionContainer {
                    Text(
                        text = "${stringResource(id = R.string.artist_popularity)}: ${data.popularity}",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.alpha(alpha = 0.8f)
                    )
                }
                FilledTonalButton(
                    modifier = Modifier.padding(start = 8.dp),
                    onClick = { ChromeCustomTabsUtil.openUrl(data.externalUrls.spotify!!) },
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = Color(
                            red = 30,
                            green = 215,
                            blue = 96
                        ).harmonizeWithPrimary(),
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
        }

        item {
            Text(
                text = stringResource(id = R.string.artist_top_tracks),
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.alpha(alpha = 0.8f)
            )
            HorizontalDivider(modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp))
        }

        if (topTracks.value != null) {
            items(topTracks.value!!.size) { index ->
                val track = topTracks.value!![index]
                val taskName =
                    "${track.name} - ${track.artists.joinToString(", ") { it.name }}"
                TrackComponent(
                    contentModifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                    songName = track.name,
                    artists = track.artists.joinToString(", ") { it.name },
                    spotifyUrl = track.externalUrls.spotify ?: "",
                    isExplicit = track.explicit,
                    onClick = {
                        trackDownloadCallback(track.externalUrls.spotify ?: "", taskName)
                    }
                )
            }
        }
    }
}