package com.bobbyesp.color.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.palette.graphics.Palette
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult

object PaletteGenerator {
    fun Bitmap.getDominantColor(): Color? {
        Palette.from(this).generate().let { palette ->
            val color = palette.vibrantSwatch?.rgb?.let { Color(it) }
            Log.i("PaletteGenerator", "getDominantColor: $color")
            return color
        }
    }

    suspend fun loadBitmapFromUrl(context: Context, url: String): Bitmap? {
        val imageLoader = ImageLoader(context)
        val request = ImageRequest.Builder(context)
            .data(url)
            .allowHardware(false)
            .build()

        val result = imageLoader.execute(request)

        // Verify is the image is success and a BitmapDrawable
        if (result is SuccessResult) {
            val drawable = result.drawable
            if (drawable is BitmapDrawable) {
                return drawable.bitmap
            } else {
                Log.w("PaletteGenerator", "loadBitmapFromUrl: Drawable is not a BitmapDrawable")
            }
        } else {
            Log.e("PaletteGenerator", "loadBitmapFromUrl: Failed to load image")
        }
        return null
    }
}