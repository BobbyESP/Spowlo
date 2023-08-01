package com.bobbyesp.spowlo.utils.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.bobbyesp.spowlo.R

@Composable
fun unknownText(): String {
    return stringResource(id = R.string.unknown)
}