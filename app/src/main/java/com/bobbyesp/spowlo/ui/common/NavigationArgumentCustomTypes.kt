package com.bobbyesp.spowlo.ui.common

import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.navigation.NavType
import com.bobbyesp.spowlo.data.local.model.SelectedSong
import kotlinx.serialization.json.Json

val SelectedSongParamType = object : NavType<SelectedSong>(isNullableAllowed = false) {
    override fun get(bundle: Bundle, key: String): SelectedSong? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            bundle.getParcelable("localSelectedSong", SelectedSong::class.java)
        } else {
            bundle.getParcelable("localSelectedSong")
        }
    }
    override fun put(bundle: Bundle, key: String, value: SelectedSong) {
        bundle.putParcelable(key, value)
    }

    override fun parseValue(value: String): SelectedSong {
        return Json.decodeFromString(Uri.decode(value))
    }

}