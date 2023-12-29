package com.bobbyesp.utilities.utilities

import androidx.annotation.ChecksSdkIntAtLeast

object AndroidVersion {
    @ChecksSdkIntAtLeast(parameter = 0)
    fun isAndroidVersionOrLater(version: Int): Boolean {
        return android.os.Build.VERSION.SDK_INT >= version
    }

    @ChecksSdkIntAtLeast(parameter = 0)
    fun isAndroidVersionOrOlder(version: Int): Boolean {
        return android.os.Build.VERSION.SDK_INT <= version
    }
}