package com.bobbyesp.spowlo.presentation.components.image

import android.content.Context
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ErrorOutline
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil.ImageLoader
import com.bobbyesp.spowlo.R
import com.bobbyesp.ui.components.others.PlaceholderCreator
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.coil.CoilImage
import com.skydoves.landscapist.coil.CoilImageState
import com.skydoves.landscapist.coil.LocalCoilImageLoader

@Composable
fun AsyncImage(
    modifier: Modifier = Modifier,
    imageModel: Any? = null,
    imageModifier: Modifier = Modifier,
    shape: Shape = MaterialTheme.shapes.small,
    placeholder: ImageVector = Icons.Rounded.MusicNote,
    context: Context = LocalContext.current,
    imageLoader: ImageLoader? = LocalCoilImageLoader.current,
    onSuccessData: (CoilImageState.Success) -> Unit = { _ -> }
) {
    val imageUrl: Any? by remember(imageModel) {
        mutableStateOf(imageModel)
    }

    Box(
        modifier = modifier.clip(shape),
        contentAlignment = Alignment.Center,
    ) {
        CoilImage(
            modifier = Modifier.fillMaxSize(),
            imageModel = { imageUrl },
            imageOptions = ImageOptions(
                contentDescription = null,
                contentScale = ContentScale.Crop
            ),
            onImageStateChanged = { state ->
                if (state is CoilImageState.Success) {
                    onSuccessData(state)
                }
            },
            loading = {
                PlaceholderCreator(
                    modifier = imageModifier
                        .fillMaxSize(),
                    icon = placeholder,
                    colorful = false,
                    contentDescription = "Song cover placeholder"
                )
            },
            failure = {
                PlaceholderCreator(
                    modifier = imageModifier
                        .fillMaxSize(),
                    icon = Icons.Rounded.ErrorOutline,
                    colorful = false,
                    contentDescription = "Song cover failed to load"
                )
            },
            previewPlaceholder = R.drawable.bones_imaginedragons,
            imageLoader = { imageLoader ?: ImageLoader(context) },
        )
    }
}