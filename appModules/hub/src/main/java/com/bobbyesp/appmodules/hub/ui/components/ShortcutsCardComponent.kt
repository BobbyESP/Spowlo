package com.bobbyesp.appmodules.hub.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bobbyesp.appmodules.core.objects.ui_components.UiItem
import com.bobbyesp.appmodules.hub.ui.clickableHubItem

@Composable
fun ShortcutsCardComponent(
    item: UiItem
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(
                3.dp
            )
        ), modifier = Modifier
            .height(56.dp)
            .fillMaxWidth()
    ) {
        Row(Modifier.clickableHubItem(item)) {
            PreviewableAsyncImage(
                imageUrl = item.images?.main?.uri,
                placeholderType = item.images?.main?.placeholder.toPlaceholderType(),
                modifier = Modifier.size(56.dp)
            )
            Text(
                item.text!!.title!!,
                fontSize = 13.sp,
                lineHeight = 18.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .padding(horizontal = 8.dp)
                    .fillMaxWidth()
            )
        }
    }
}