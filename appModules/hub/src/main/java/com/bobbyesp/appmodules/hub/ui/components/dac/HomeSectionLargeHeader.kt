package com.bobbyesp.appmodules.hub.ui.components.dac

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bobbyesp.appmodules.core.objects.ui_components.UiItem
import com.bobbyesp.appmodules.hub.ui.clickableHubItem
import com.bobbyesp.appmodules.hub.ui.components.PreviewableAsyncImage
import com.bobbyesp.appmodules.hub.ui.components.toPlaceholderType
import com.bobbyesp.uisdk.components.text.MediumText
import com.bobbyesp.uisdk.components.text.SubtextOverline

@Composable
fun HomeSectionLargeHeader(
    item: UiItem,
) {
    Row(
        Modifier
            .padding(vertical = 8.dp)
            .clickableHubItem(item)) {
        PreviewableAsyncImage(
            imageUrl = item.images?.main?.uri,
            placeholderType = item.images?.main?.placeholder.toPlaceholderType(),
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
        )

        Column(
            Modifier
                .padding(horizontal = 12.dp)
                .align(Alignment.CenterVertically)) {
            SubtextOverline(item.text!!.subtitle!!.uppercase(), modifier = Modifier)
            MediumText(item.text?.title!!, modifier = Modifier.padding(top = 2.dp), fontSize = 21.sp)
        }
    }
}