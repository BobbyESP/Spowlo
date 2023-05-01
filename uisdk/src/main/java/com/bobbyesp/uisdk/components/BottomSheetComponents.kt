package com.bobbyesp.uisdk.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun BottomSheetHandle(
    modifier: Modifier = Modifier
) {
    Divider(
        modifier = modifier.width(32.dp).padding(vertical = 14.dp).clip(CircleShape),
        thickness = 4.dp,
        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(0.4f)
    )
}

@Composable
fun BottomSheetHeader(
    modifier: Modifier = Modifier,
    text: String
) {
    Text(
        text,
        fontSize = 22.sp,
        color = MaterialTheme.colorScheme.onSurface,
        textAlign = TextAlign.Center,
        modifier = modifier.fillMaxWidth()
    )
}

@Composable
fun BottomSheetSubtitle(
    modifier: Modifier = Modifier,
    text: AnnotatedString
) {
    Text(
        text,
        fontSize = 16.sp,
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
        textAlign = TextAlign.Center,
        modifier = modifier.fillMaxWidth()
    )
}