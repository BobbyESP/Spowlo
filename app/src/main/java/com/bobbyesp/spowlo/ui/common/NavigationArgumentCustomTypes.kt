package com.bobbyesp.spowlo.ui.common

import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import androidx.navigation.NavType
import com.bobbyesp.spowlo.App.Companion.json
import com.bobbyesp.utilities.ext.getClassOfType

@Suppress("DEPRECATION")
inline fun <reified T : Parcelable> parcelableTypeOf() = object : NavType<T>(isNullableAllowed = false) {
    override fun get(bundle: Bundle, key: String): T? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            bundle.getParcelable(key, getClassOfType<T>())
        } else {
            bundle.getParcelable(key)
        }
    }

    override fun put(bundle: Bundle, key: String, value: T) {
        bundle.putParcelable(key, value)
    }

    override fun parseValue(value: String): T {
        return json.decodeFromString(Uri.decode(value))
    }
}