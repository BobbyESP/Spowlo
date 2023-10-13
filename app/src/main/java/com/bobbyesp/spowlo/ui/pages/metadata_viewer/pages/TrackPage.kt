package com.bobbyesp.spowlo.ui.pages.metadata_viewer.pages

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.SaverScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.adamratzman.spotify.models.AudioFeatures
import com.adamratzman.spotify.models.Track
import com.bobbyesp.spowlo.R
import com.bobbyesp.spowlo.features.spotify_api.data.remote.SpotifyApiRequests
import com.bobbyesp.spowlo.ui.common.AsyncImageImpl
import com.bobbyesp.spowlo.ui.components.HorizontalDivider
import com.bobbyesp.spowlo.ui.components.MarqueeText
import com.bobbyesp.spowlo.ui.components.songs.metadata_viewer.ExtraInfoCard
import com.bobbyesp.spowlo.ui.components.songs.metadata_viewer.TrackComponent
import com.bobbyesp.spowlo.ui.pages.metadata_viewer.binders.dataStringToString
import com.bobbyesp.spowlo.utils.GeneralTextUtils
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@OptIn(ExperimentalSerializationApi::class)
@Composable
fun TrackPage(
    data: Track,
    modifier: Modifier,
    trackDownloadCallback: (String, String) -> Unit,
) {
    val localConfig = LocalConfiguration.current
    var audioFeatures by rememberSaveable(stateSaver = AudioFeaturesSaver) {
        mutableStateOf(null)
    }
    var trackData by rememberSaveable(stateSaver = TrackSaver) {
        mutableStateOf(data)
    }

    LaunchedEffect(Unit) {
        if (audioFeatures == null) {
            val feats = SpotifyApiRequests.providesGetAudioFeatures(data.id)
            audioFeatures = feats
        }
        if (trackData != data) {
            trackData = data
        }
    }

    Column(
        modifier = modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .clip(MaterialTheme.shapes.extraSmall)
                .fillMaxWidth()
                .padding(bottom = 6.dp), contentAlignment = Alignment.Center
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
                model = trackData.album.images[0].url,
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
                    text = trackData.name,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.headlineMedium
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            SelectionContainer {
                Text(
                    text = trackData.artists.joinToString(", ") { it.name },
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
                        data = trackData.type, additional = trackData.album.releaseDate?.year.toString()
                    ),
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.alpha(alpha = 0.8f)
                )
            }
        }
        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp))

        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            val taskName = StringBuilder().append(trackData.name).append(" - ")
                .append(trackData.artists.joinToString(", ") { it.name }).toString()
            TrackComponent(contentModifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                songName = trackData.name,
                artists = trackData.artists.joinToString(", ") { it.name },
                spotifyUrl = trackData.externalUrls.spotify!!,
                isExplicit = trackData.explicit,
                onClick = { trackDownloadCallback(trackData.externalUrls.spotify!!, taskName) })
        }
        Spacer(modifier = Modifier.padding(vertical = 8.dp))
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                ExtraInfoCard(
                    headlineText = stringResource(id = R.string.track_popularity),
                    bodyText = trackData.popularity.toString(),
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(16.dp))
                ExtraInfoCard(
                    headlineText = stringResource(id = R.string.track_duration),
                    bodyText = GeneralTextUtils.convertDuration(trackData.durationMs.toDouble()),
                    modifier = Modifier.weight(1f)
                )
            }
            AnimatedVisibility(visible = audioFeatures != null) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    ExtraInfoCard(
                        headlineText = stringResource(id = R.string.loudness),
                        bodyText = audioFeatures!!.loudness.toString(),
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    ExtraInfoCard(
                        headlineText = stringResource(id = R.string.tempo),
                        bodyText = audioFeatures!!.tempo.toString() + " BPM",
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

//create a saver for the audio features
@ExperimentalSerializationApi
object AudioFeaturesSaver : Saver<AudioFeatures?, String> {
    override fun restore(value: String): AudioFeatures? {
        return Json.decodeFromString(value)
    }

    override fun SaverScope.save(value: AudioFeatures?): String {
        return Json.encodeToString(value)
    }
}

@ExperimentalSerializationApi
object TrackSaver : Saver<Track, String> {
    override fun restore(value: String): Track {
        return Json.decodeFromString(value)
    }

    override fun SaverScope.save(value: Track): String {
        return Json.encodeToString(value)
    }
}