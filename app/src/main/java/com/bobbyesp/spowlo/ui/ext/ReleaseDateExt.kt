package com.bobbyesp.spowlo.ui.ext

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.adamratzman.spotify.models.ReleaseDate
import com.bobbyesp.spowlo.R

@Composable
fun ReleaseDate?.toCompleteString(): String {
    return this?.run {
        when {
            day != null && month != null -> "$day/$month/$year"
            month != null -> "$month/$year"
            else -> "$year"
        }
    } ?: stringResource(id = R.string.unknown)
}
