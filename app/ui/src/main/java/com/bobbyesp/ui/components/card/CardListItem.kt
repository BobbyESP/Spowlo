package com.bobbyesp.ui.components.card

import androidx.compose.foundation.clickable
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@Composable
fun CardListItem(
    modifier: Modifier = Modifier,
    leadingContentIcon: ImageVector,
    headlineContentText: String,
    applySemiBoldFontWeight: Boolean = true,
    onClick: () -> Unit,
    contentDescription: String? = null
) {
    Card(
        modifier = modifier, colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(
                6.dp
            )
        )
    ) {
        ListItem(
            leadingContent = {
                Icon(
                    imageVector = leadingContentIcon,
                    contentDescription = contentDescription
                )
            }, headlineContent = {
                Text(
                    text = headlineContentText,
                    fontWeight = if (applySemiBoldFontWeight) FontWeight.SemiBold else FontWeight.Normal,
                    overflow = TextOverflow.Ellipsis,
                )
            }, modifier = Modifier.clickable(onClick = onClick),
            colors = ListItemDefaults.colors(
                leadingIconColor = MaterialTheme.colorScheme.primary,
                containerColor = Color.Transparent,
            )
        )
    }
}