package com.bobbyesp.spowlo.ui.components.others

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.bobbyesp.spowlo.R
import com.bobbyesp.spowlo.ui.components.images.AsyncImageImpl

@Composable
fun StackedProfilePictures(
    profilePhotos: List<String>,
    modifier: Modifier = Modifier,
    stackSpacing: Int = 4
) {
    Box(
        modifier = modifier,
    ) {
        profilePhotos.forEachIndexed { index, photoUrl ->
            val overlapOffset = index * stackSpacing / 1.5
            ProfilePhoto(
                modifier = Modifier
                    .padding(start = overlapOffset.dp)
                    .graphicsLayer(
                        translationX = -overlapOffset.toFloat(),
                    )
                    .zIndex(-index.toFloat()),
                photoUrl = photoUrl,
            )
        }
    }
}

@Composable
fun ProfilePhoto(
    modifier: Modifier = Modifier,
    photoUrl: String,
) {
    Box(
        modifier = modifier
            .size(24.dp)
            .clip(CircleShape)
            .background(color = MaterialTheme.colorScheme.secondary)
    ) {
        AsyncImageImpl(
            model = photoUrl,
            contentDescription = stringResource(id = R.string.profile_picture),
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds
        )
    }
}