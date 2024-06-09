package com.bobbyesp.utilities.navigation

import android.os.Build
import android.os.Parcelable
import androidx.navigation.NavBackStackEntry

inline fun <reified T : Parcelable> NavBackStackEntry.getParcelable(key: String): T? {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        this.arguments?.getParcelable(
            key,
            T::class.java
        )
    } else {
        this.arguments?.getParcelable(key) as? T
    }
}
