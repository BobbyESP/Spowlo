package com.bobbyesp.appmodules.core.navigation.ext.delegates

import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.core.content.FileProvider
import coil.annotation.ExperimentalCoilApi
import coil.imageLoader
import coil.request.CachePolicy
import coil.request.ImageRequest

class ShareDispatcher (
    private val context: Context,
) {
    @OptIn(ExperimentalCoilApi::class)
    fun share(url: String, humanReadableTitle: String, pictureUrl: String? = null) {
        if (pictureUrl != null) {
            context.imageLoader.enqueue(
                ImageRequest.Builder(context)
                    .diskCachePolicy(CachePolicy.ENABLED)
                    .memoryCachePolicy(CachePolicy.DISABLED)
                    .data(pictureUrl)
                    .listener(onSuccess = { _, result ->
                        if (result.diskCacheKey != null) {
                            val path = context.imageLoader.diskCache?.get(result.diskCacheKey!!)?.data?.toFile()

                            if (path != null) {
                                shareInternal(url, humanReadableTitle, pictureUri = FileProvider.getUriForFile(context, "bruhcollective.itaysonlab.jetisteam.files", path))
                            } else {
                                shareInternal(url, humanReadableTitle, null)
                            }
                        } else {
                            shareInternal(url, humanReadableTitle, null)
                        }
                    }, onError = { _, error ->
                        error.throwable.printStackTrace()
                    })
                    .build()
            )
        } else {
            shareInternal(url, humanReadableTitle, null)
        }
    }

    private fun shareInternal(url: String, humanReadableTitle: String, pictureUri: Uri?) {
        context.startActivity(
            Intent.createChooser(Intent().apply {
                action = Intent.ACTION_SEND

                putExtra(Intent.EXTRA_TEXT, url)
                putExtra(Intent.EXTRA_TITLE, humanReadableTitle)

                if (pictureUri != null) {
                    setDataAndType(pictureUri, "text/plain")
                    clipData = ClipData.newRawUri(null, pictureUri)
                } else {
                    type = "text/plain"
                }

                flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            }, null)
        )
    }
}

val LocalShareDispatcher = staticCompositionLocalOf<ShareDispatcher> { error("LocalShareDispatcher is not installed!") }