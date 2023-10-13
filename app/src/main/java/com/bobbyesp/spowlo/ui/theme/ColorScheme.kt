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

@Composable
infix fun Color.withNight(nightColor: Color): Color {
    return if (LocalDarkTheme.current.isDarkTheme()) nightColor else this
}

//Create a blur effect

@Composable
fun BlurEffect(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.surface,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .background(color = color)
            .padding(16.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(
                color = Color.Transparent,
                shape = RoundedCornerShape(16.dp)
            )
    ) {
        content()
    }
}

const val DEFAULT_SEED_COLOR = 0xFF415f76.toInt()

@Composable
fun contraryColor(): Color {
    return if (isSystemInDarkTheme()) Color.White else Color.Black
}