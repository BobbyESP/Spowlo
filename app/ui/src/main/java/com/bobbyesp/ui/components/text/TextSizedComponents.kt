package com.bobbyesp.ui.components.text

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp

@Composable
fun MediumText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    fontWeight: FontWeight = FontWeight.Bold,
    lineHeight: TextUnit = TextUnit.Unspecified,
    maxLines: Int = 1,
    fontSize: TextUnit = 16.sp,
    textAlign: TextAlign? = null,
) {
    Text(
        text,
        textAlign = textAlign,
        color = color,
        fontSize = fontSize,
        fontWeight = fontWeight,
        maxLines = maxLines,
        lineHeight = lineHeight,
        overflow = TextOverflow.Ellipsis,
        modifier = modifier
    )
}

@Composable
fun Subtext(
    text: String,
    modifier: Modifier = Modifier,
    fontSize: TextUnit = 12.sp,
    maxLines: Int = 2,
    textAlign: TextAlign? = null,
) {
    Text(
        text,
        textAlign = textAlign,
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
        fontSize = fontSize,
        lineHeight = 18.sp,
        maxLines = maxLines,
        overflow = TextOverflow.Ellipsis,
        modifier = modifier
    )
}