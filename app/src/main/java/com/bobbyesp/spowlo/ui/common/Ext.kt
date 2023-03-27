package com.bobbyesp.spowlo.ui.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.bobbyesp.spowlo.utils.PreferencesUtil.getBoolean
import com.bobbyesp.spowlo.utils.PreferencesUtil.getInt
import com.bobbyesp.spowlo.utils.PreferencesUtil.getString

inline val String.booleanState
    @Composable get() =
        remember { mutableStateOf(this.getBoolean()) }

inline val String.stringState
    @Composable get() =
        remember { mutableStateOf(this.getString()) }

inline val String.intState
    @Composable get() = remember {
        mutableStateOf(this.getInt())
    }

fun String.containsEllipsis(): Boolean {
    return this.contains("…")
}
