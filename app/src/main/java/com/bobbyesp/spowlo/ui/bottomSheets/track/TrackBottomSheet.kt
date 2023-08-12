package com.bobbyesp.spowlo.ui.bottomSheets.track

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Lyrics
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.adamratzman.spotify.models.SimpleTrack
import com.adamratzman.spotify.models.Track
import com.bobbyesp.spowlo.R
import com.bobbyesp.spowlo.data.local.model.SelectedSong
import com.bobbyesp.spowlo.ui.common.LocalNavController
import com.bobbyesp.spowlo.ui.common.Route
import com.bobbyesp.spowlo.ui.components.bottomsheets.BottomSheet
import com.bobbyesp.spowlo.ui.components.images.AsyncImageImpl
import com.bobbyesp.spowlo.ui.components.lazygrid.GridMenuItem
import com.bobbyesp.spowlo.ui.components.lazygrid.VerticalGridMenu
import com.bobbyesp.spowlo.utils.localAsset
import com.bobbyesp.spowlo.utils.notifications.ToastUtil

@Composable
fun TrackBottomSheet(track: Track? = null, simpleTrack: SimpleTrack? = null, onDismiss: () -> Unit) {

    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val uriHandler = LocalUriHandler.current
    val navController = LocalNavController.current

    val spotifyUrlNotNull = track?.externalUrls?.spotify != null || simpleTrack?.externalUrls?.spotify != null
    val spotifyUrl = track?.externalUrls?.spotify ?: simpleTrack?.externalUrls?.spotify

    val trackName = track?.name ?: simpleTrack?.name ?: ""
    val trackArtists = track?.artists ?: simpleTrack?.artists ?: emptyList()
    val trackArtistsString = trackArtists.joinToString(", ") { artist -> artist.name }
    val trackImage = track?.album?.images?.firstOrNull()?.url

    BottomSheet(onDismiss = onDismiss) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (trackImage != null) AsyncImageImpl(
                modifier = Modifier
                    .size(50.dp)
                    .aspectRatio(
                        1f, matchHeightConstraintsFirst = true
                    )
                    .clip(MaterialTheme.shapes.extraSmall),
                model = track.album.images.firstOrNull()!!.url,
                contentDescription = stringResource(
                    id = R.string.track_artwork
                )
            )
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = trackName, fontWeight = FontWeight.Bold, maxLines = 1
                )
                Text(
                    text = trackArtistsString,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Normal,
                    modifier = Modifier.alpha(alpha = 0.6f),
                    maxLines = 1
                )
            }
        }
        HorizontalDivider()
        VerticalGridMenu(
            modifier = Modifier,
            contentPadding = PaddingValues(0.dp)
        ) {
            GridMenuItem(
                icon = { localAsset(id = R.drawable.spotify_logo) },
                title = { stringResource(id = R.string.open_in_spotify) },
                onClick = {
                    uriHandler.openUri(spotifyUrl!!)
                },
                enabled = spotifyUrlNotNull
            )
            GridMenuItem(
                icon = Icons.Default.Download,
                title = { stringResource(id = R.string.download) },
                onClick = { }
            )
            GridMenuItem(
                icon = Icons.Default.ContentCopy,
                title = { stringResource(id = R.string.copy_link) },
                onClick = {
                    try {
                        clipboardManager.setText(AnnotatedString(spotifyUrl!!))
                        ToastUtil.makeToast(context, R.string.copied_to_clipboard)
                    } catch (e: Exception) {
                        Log.e("TrackBottomSheet", "Failed to copy link", e)
                        ToastUtil.makeToast(context, R.string.copy_failed)
                        return@GridMenuItem
                    }
                }, enabled = spotifyUrlNotNull
            )
            GridMenuItem(
                icon = Icons.Default.Lyrics,
                title = { stringResource(id = R.string.lyrics) },
                onClick = {
                    onDismiss()

                    val mainArtist = trackArtists.first().name

                    val selectedSongParcel = SelectedSong(
                        name = trackName,
                        mainArtist = mainArtist,
                        localSongPath = null,
                    )

                    navController.navigate(
                        Route.SelectedSongLyrics.createRoute(
                            selectedSongParcel
                        )
                    )
                }
            )
        }
    }
}