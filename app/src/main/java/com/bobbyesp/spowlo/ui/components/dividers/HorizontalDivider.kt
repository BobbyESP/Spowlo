package com.bobbyesp.spowlo.ui.components.dividers

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun HorizontalDivider(
    modifier: Modifier = Modifier,
    color: Color = DividerDefaults.color
) {
    HorizontalDivider(
        modifier = modifier
            .fillMaxWidth()
            .size(DividerDefaults.Thickness),
        color = color
    )
}