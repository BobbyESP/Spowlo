package com.bobbyesp.spowlo.ui.components.images

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lyrics
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bobbyesp.spowlo.ui.theme.SpowloTheme

@Stable
@Composable
fun PlaceholderCreator(
    modifier: Modifier = Modifier,
    icon: ImageVector?,
    colorful: Boolean,
    contentDescription: String? = null
) {
    Surface(
        tonalElevation = if (colorful) 0.dp else 8.dp,
        color = if (colorful) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
        modifier = modifier
    ) {
        if (icon != null) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Icon(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp),
                    imageVector = icon,
                    contentDescription = contentDescription,
                    tint = if (colorful) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                )
            }
        }
    }
}

@Preview
@Preview(uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun PlaceholderCreatorPreview() {
    SpowloTheme {
        PlaceholderCreator(
            modifier = Modifier
                .width(200.dp)
                .aspectRatio(1f),
            icon = Icons.Default.Lyrics,
            colorful = true,
            contentDescription = "Song cover"
        )
    }
}

@Preview
@Preview(uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun PlaceholderCreatorPreviewNonColourful() {
    SpowloTheme {
        PlaceholderCreator(
            modifier = Modifier
                .width(200.dp)
                .aspectRatio(1f),
            icon = Icons.Default.Lyrics,
            colorful = false,
            contentDescription = "Song cover"
        )
    }
}