package com.bobbyesp.uisdk

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

val LocalFloatingBarInset = compositionLocalOf { 0.dp }

val FloatingWindowInsets @Composable get() = WindowInsets(bottom = LocalFloatingBarInset.current)
val FloatingWindowInsetsAsPaddings @Composable get() = PaddingValues(bottom = LocalFloatingBarInset.current)

@Composable
fun floatingWindowInsetsAsPaddings(additionalPadding: Dp) = PaddingValues(top = additionalPadding, start = additionalPadding, end = additionalPadding, bottom = additionalPadding + LocalFloatingBarInset.current)