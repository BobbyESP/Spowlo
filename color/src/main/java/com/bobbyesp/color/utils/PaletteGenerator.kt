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
    suspend fun convertImageUrlToBitmap(
        imageUrl: String,
        context: Context
    ): Bitmap? {
        val loader = ImageLoader(context = context)
        val request = ImageRequest.Builder(context = context)
            .data(imageUrl)
            .allowHardware(false)
            .build()
        val imageResult = loader.execute(request = request)
        return if (imageResult is SuccessResult) {
            (imageResult.drawable as BitmapDrawable).bitmap
        } else {
            null
        }
    }

    fun extractColorsFromBitmap(bitmap: Bitmap): Map<ColorType, Color> {
        return mapOf(
            ColorType.VIBRANT to parseColorSwatch(
                Palette.from(bitmap).generate().vibrantSwatch
            ),
            ColorType.DARK_VIBRANT to parseColorSwatch(
                Palette.from(bitmap).generate().darkVibrantSwatch
            ),
            ColorType.ON_DARK_VIBRANT to parseColorSwatch(
                Palette.from(bitmap).generate().darkVibrantSwatch
            )
        )
    }

    suspend fun fromImageUrlToExtractedColors(imageUrl: String, context: Context): Map<ColorType, Color>? {
        val imageBitmap = try {
            convertImageUrlToBitmap(imageUrl, context)
        } catch (e: Exception) {
            Log.i("PaletteGenerator", "Error: ${e.message}")
            null
        }

        return if(imageBitmap != null) {
            extractColorsFromBitmap(imageBitmap)
        } else {
            null
        }
    }
    private fun parseColorSwatch(color: Palette.Swatch?): Color {
        return if (color != null) {
            val parsedColor = Integer.toHexString(color.rgb)
            return parsedColor.toColor()
        } else {
            "#000000".toColor()
        }
    }

    private fun parseBodyColor(color: Int?): Color {
        return if (color != null) {
            val parsedColor = Integer.toHexString(color)
            "#$parsedColor".toColor()
        } else {
            "#FFFFFF".toColor()
        }
    }
}

fun String.toColor(): Color {
    return Color(android.graphics.Color.parseColor(this))
}

enum class ColorType {
    VIBRANT,
    DARK_VIBRANT,
    ON_DARK_VIBRANT
}