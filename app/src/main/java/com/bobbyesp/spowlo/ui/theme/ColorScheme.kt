package com.bobbyesp.spowlo.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.bobbyesp.spowlo.ui.common.LocalDarkTheme

const val DEFAULT_SEED_COLOR = 0xFF415f76.toInt()

@Composable
fun oppositeColor(): Color {
    return if (isSystemInDarkTheme()) Color.White else Color.Black
}