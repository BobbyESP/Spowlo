package com.bobbyesp.appmodules.hub.ui.components

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Album
import androidx.compose.material.icons.rounded.Audiotrack
import androidx.compose.material.icons.rounded.Mic
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.PlaylistPlay
import androidx.compose.material.icons.rounded.Podcasts
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter

@Composable
fun PreviewableAsyncImage(
    imageUrl: String?,
    placeholderType: PlaceholderType?,
    modifier: Modifier
) {
    if (imageUrl.isNullOrEmpty() || imageUrl == "https://i.scdn.co/image/" || imageUrl.startsWith("spotify:mosaic")) {
        Box(modifier) {
            ImagePreview(placeholderType, modifier)
        }
    } else {
        val painter = rememberAsyncImagePainter(
            model = imageUrl,
            contentScale = ContentScale.Crop,
        )
        Crossfade(targetState = painter.state, animationSpec = tween(400), label = "") { state ->
            when (state) {
                is AsyncImagePainter.State.Success -> {
                    Image(
                        painter = painter,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = modifier
                    )
                }

                else -> {
                    Box(modifier) {
                        ImagePreview(placeholderType, Modifier.fillMaxSize())
                        Image(
                            painter = painter,
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = modifier
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PreviewableSyncImage(
    imageBitmap: ImageBitmap?,
    placeholderType: PlaceholderType.None,
    modifier: Modifier
) {
    if (imageBitmap != null) {
        Image(
            bitmap = imageBitmap,
            contentScale = ContentScale.Crop,
            contentDescription = null,
            modifier = modifier
        )
    } else {
        ImagePreview(placeholderType, modifier)
    }
}

@Stable
@Composable
fun ImagePreview(
    type: PlaceholderType?,
    modifier: Modifier
) {
    ImagePreview(
        if (type != PlaceholderType.None) placeholderToIcon(type) else null,
        false,
        modifier
    )
}

@Stable
@Composable
fun ImagePreview(
    of: ImageVector?,
    colorful: Boolean,
    modifier: Modifier
) {
    Surface(
        tonalElevation = if (colorful) 0.dp else 8.dp,
        color = if (colorful) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
        modifier = modifier
    ) {
        if (of != null) {
            Box(Modifier.fillMaxSize()) {
                Icon(
                    imageVector = of,
                    tint = if (colorful) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp)
                )
            }
        }
    }
}

private fun placeholderToIcon(type: PlaceholderType?) = when (type) {
    PlaceholderType.Artist -> Icons.Rounded.Mic
    PlaceholderType.Album -> Icons.Rounded.Album
    PlaceholderType.Podcasts -> Icons.Rounded.Podcasts
    PlaceholderType.Playlist -> Icons.Rounded.PlaylistPlay
    PlaceholderType.User -> Icons.Rounded.Person
    else -> Icons.Rounded.Audiotrack
}

sealed class PlaceholderType(val type: String) {
    object Artist : PlaceholderType("artist")
    object Album : PlaceholderType("album")
    object Podcasts : PlaceholderType("podcasts")
    object Playlist : PlaceholderType("playlist")
    object User : PlaceholderType("user")
    object Track : PlaceholderType("track")
    object None : PlaceholderType("none")
}

