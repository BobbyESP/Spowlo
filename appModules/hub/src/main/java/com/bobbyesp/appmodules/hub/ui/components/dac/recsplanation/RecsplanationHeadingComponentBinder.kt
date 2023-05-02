package com.bobbyesp.appmodules.hub.ui.components.dac.recsplanation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bobbyesp.appmodules.hub.ui.components.PlaceholderType
import com.bobbyesp.appmodules.hub.ui.components.PreviewableAsyncImage
import com.bobbyesp.uisdk.components.text.MediumText
import com.bobbyesp.uisdk.components.text.SubtextOverline
import com.spotify.home.dac.component.v1.proto.RecsplanationHeadingComponent

@Composable
fun RecsplanationHeadingComponentBinder(
    item: RecsplanationHeadingComponent,
    onNavigateTo: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .clickable {
                onNavigateTo(item.navigateUri)
            }
            .padding(horizontal = 16.dp, vertical = 16.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        PreviewableAsyncImage(
            imageUrl = item.imageUri,
            placeholderType = PlaceholderType.User,
            modifier = Modifier
                .size(48.dp)
                .clip(MaterialTheme.shapes.medium)
        )
        Column(
            Modifier
                .padding(horizontal = 12.dp)
                .align(Alignment.CenterVertically)
        ) {
            SubtextOverline(item.subtitle.uppercase(), modifier = Modifier)
            MediumText(item.title, modifier = Modifier.padding(top = 2.dp), fontSize = 21.sp)
        }
    }
}