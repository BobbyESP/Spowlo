package com.bobbyesp.ui.components.text.bottomSheet

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp

@Composable
fun BottomSheetHeader(
    modifier: Modifier = Modifier,
    text: String
) {
    Text(
        text,
        fontSize = 24.sp,
        color = MaterialTheme.colorScheme.onSurface,
        textAlign = TextAlign.Center,
        fontWeight = FontWeight.Medium,
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