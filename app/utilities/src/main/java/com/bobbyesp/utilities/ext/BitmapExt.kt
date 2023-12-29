package com.bobbyesp.utilities.ext

import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import java.io.ByteArrayOutputStream

fun Bitmap.toByteArray(format: CompressFormat = CompressFormat.PNG): ByteArray {
    val stream = ByteArrayOutputStream()
    this.compress(format, 100, stream)
    return stream.toByteArray()
}