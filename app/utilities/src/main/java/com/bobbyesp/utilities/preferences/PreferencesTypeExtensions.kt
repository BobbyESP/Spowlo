package com.bobbyesp.utilities.preferences

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.bobbyesp.utilities.preferences.Preferences.getBoolean
import com.bobbyesp.utilities.preferences.Preferences.getInt
import com.bobbyesp.utilities.preferences.Preferences.getString

inline val String.booleanState
    @Composable get() =
        remember { mutableStateOf(this.getBoolean()) }

inline val String.stringState
    @Composable get() =
        remember { mutableStateOf(this.getString()) }

inline val String.intState
    @Composable get() = remember {
        mutableIntStateOf(this.getInt())
    }