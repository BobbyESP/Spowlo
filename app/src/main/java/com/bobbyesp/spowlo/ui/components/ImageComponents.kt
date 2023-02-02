package com.bobbyesp.spowlo.ui.components

import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import com.bobbyesp.spowlo.R
import com.bobbyesp.spowlo.ui.common.AsyncImageImpl

@Composable
fun MediaImage(modifier: Modifier = Modifier, imageModel: String, isAudio: Boolean = false) {
    AsyncImageImpl(
        modifier = modifier
            .aspectRatio(
                if (!isAudio) 16f / 9f else 1f, matchHeightConstraintsFirst = true
            )
            .clip(MaterialTheme.shapes.extraSmall),
        model = imageModel.ifEmpty { R.drawable.sample1 },
        contentDescription = null,
        contentScale = ContentScale.Crop,
    )
}