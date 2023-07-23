package com.bobbyesp.spowlo.ui.components.cards

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lyrics
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bobbyesp.spowlo.ui.theme.SpowloTheme

@Composable
fun CardListItem(
    modifier: Modifier = Modifier,
    leadingContentIcon: ImageVector,
    headlineContentText: String,
    applySemiBoldFontWeight: Boolean = true,
    onClick: () -> Unit,
) {
    Card(
        modifier = modifier, colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(
                3.dp
            )
        )
    ) {
        ListItem(
            leadingContent = {
            Icon(
                imageVector = leadingContentIcon,
                contentDescription = "Leading content icon"
            )
        }, headlineContent = {
            Text(text = headlineContentText, fontWeight = if(applySemiBoldFontWeight) FontWeight.SemiBold else FontWeight.Normal)
        }, modifier = Modifier.clickable(onClick = onClick),
            colors = ListItemDefaults.colors(
                leadingIconColor = MaterialTheme.colorScheme.primary,
                containerColor = Color.Transparent,
            )
        )
    }
}

@Preview
@Preview(uiMode = UI_MODE_NIGHT_YES)
@Composable
fun CardListItemPreview() {
    SpowloTheme {
        CardListItem(
            leadingContentIcon = Icons.Default.Lyrics,
            headlineContentText = "Save lyrics to file",
            onClick = {}
        )
    }
}