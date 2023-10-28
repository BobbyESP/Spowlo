package com.bobbyesp.spowlo.ui.components.images

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.DefaultAlpha
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import coil.disk.DiskCache
import coil.memory.MemoryCache
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.bobbyesp.spowlo.App.Companion.USER_AGENT_HEADER
import com.bobbyesp.spowlo.R
import okhttp3.OkHttpClient

@Composable
fun AsyncImageImpl(
    model: Any,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    alignment: Alignment = Alignment.Center,
    contentScale: ContentScale = ContentScale.Fit,
    alpha: Float = DefaultAlpha,
    transform: (AsyncImagePainter.State) -> AsyncImagePainter.State = AsyncImagePainter.DefaultTransform,
    onState: ((AsyncImagePainter.State) -> Unit)? = null,
    colorFilter: ColorFilter? = null,
    filterQuality: FilterQuality = DrawScope.DefaultFilterQuality,
    isPreview: Boolean = false
) {
    val context = LocalContext.current

    // Create an ImageLoader if it doesn't exist yet and remember it with the current context.
    val imageLoader = ImageLoader.Builder(context).memoryCache {
        MemoryCache.Builder(context).maxSizePercent(0.05).build()
    }.diskCache {
        DiskCache.Builder().directory(context.cacheDir.resolve("image_cache")).maxSizePercent(0.2)
            .build()
    }.okHttpClient {
        OkHttpClient.Builder().build()
    }.build()

    val imageRequest =
        ImageRequest.Builder(context).addHeader("user-agent", USER_AGENT_HEADER).data(model)
            .crossfade(true).build()

    if (isPreview) {
        Image(
            painter = painterResource(R.drawable.bones_imaginedragons_testimage),
            contentDescription = contentDescription,
            modifier = modifier,
            alignment = alignment,
            contentScale = contentScale,
            alpha = alpha,
            colorFilter = colorFilter,
        )
    } else {
        AsyncImage(
            model = imageRequest,
            imageLoader = imageLoader,
            onState = onState,
            filterQuality = filterQuality,
            transform = transform,
            contentDescription = contentDescription,
            modifier = modifier,
            alignment = alignment,
            contentScale = contentScale,
            alpha = alpha,
            colorFilter = colorFilter,
        )
    }
}


@Composable
fun AsyncImageImpl(
    model: Any,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    error: Painter? = null,
    placeholder: Painter? = null,
    fallback: Painter? = null,
    onLoading: ((AsyncImagePainter.State.Loading) -> Unit)? = null,
    onSuccess: ((AsyncImagePainter.State.Success) -> Unit)? = null,
    onError: ((AsyncImagePainter.State.Error) -> Unit)? = null,
    alignment: Alignment = Alignment.Center,
    contentScale: ContentScale = ContentScale.Fit,
    alpha: Float = DefaultAlpha,
    colorFilter: ColorFilter? = null,
    filterQuality: FilterQuality = DrawScope.DefaultFilterQuality,
) {

    val context = LocalContext.current

    val imageLoader = ImageLoader.Builder(context).memoryCache {
        MemoryCache.Builder(context).maxSizePercent(0.05).build()
    }.diskCache {
        DiskCache.Builder().directory(context.cacheDir.resolve("image_cache")).maxSizePercent(0.2)
            .build()
    }.okHttpClient {
        OkHttpClient.Builder().build()
    }.build()


    val imageRequest =
        ImageRequest.Builder(context).addHeader("user-agent", USER_AGENT_HEADER).data(model)
            .crossfade(true).build()

    val placeholderPainter = placeholder ?: painterResource(R.drawable.ic_launcher_foreground)

    AsyncImage(
        model = imageRequest,
        imageLoader = imageLoader,
        filterQuality = filterQuality,
        onError = onError,
        onLoading = onLoading,
        onSuccess = onSuccess,
        fallback = fallback,
        error = error,
        placeholder = placeholderPainter,
        contentDescription = contentDescription,
        modifier = modifier,
        alignment = alignment,
        contentScale = contentScale,
        alpha = alpha,
        colorFilter = colorFilter,

        )
}

@Composable
fun loadBitmapFromUrl(url: String): Bitmap? {
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }
    val context = LocalContext.current

    LaunchedEffect(url) {
        val imageLoader = ImageLoader(context)
        val request = ImageRequest.Builder(context).data(url).target { drawable ->
            if (drawable is BitmapDrawable) {
                bitmap = drawable.bitmap
            }
        }.build()

        val result = (imageLoader.execute(request) as SuccessResult).drawable
        if (result is BitmapDrawable) {
            bitmap = result.bitmap
        }
    }

    return bitmap
}
