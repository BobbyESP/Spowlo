package com.bobbyesp.appmodules.hub.ui.components.dac.actionCards

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bobbyesp.appmodules.hub.ui.components.DynamicLikeButton
import com.bobbyesp.appmodules.hub.ui.components.DynamicPlayButton
import com.bobbyesp.appmodules.hub.ui.components.PlaceholderType
import com.bobbyesp.appmodules.hub.ui.components.PreviewableAsyncImage
import com.bobbyesp.uisdk.components.text.MediumText
import com.bobbyesp.uisdk.components.text.Subtext
import com.spotify.dac.player.v1.proto.PlayCommand

@Composable
fun SmallActionCardBinder(
    title: String,
    subtitle: String,
    navigateUri: String,
    likeUri: String,
    imageUri: String,
    imagePlaceholder: PlaceholderType,
    playCommand: PlayCommand,
    onNavigateToUri: (String) -> Unit
) {
    Card(colors = CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(
            3.dp
        )
    ), modifier = Modifier
      .padding(horizontal = 16.dp)
      .height(120.dp)
      .fillMaxWidth()
      .clickable {
        onNavigateToUri(navigateUri)
      }) {
        Row {
            PreviewableAsyncImage(
                imageUrl = imageUri, placeholderType = imagePlaceholder, modifier = Modifier
                .fillMaxHeight()
                .width(120.dp)
            )
            Box(
              Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Column(Modifier.align(Alignment.TopStart)) {
                    MediumText(text = title)
                    Subtext(text = subtitle)
                }

                Box(
                    modifier = Modifier
                      .offset(y = 4.dp)
                      .fillMaxWidth()
                      .align(Alignment.BottomStart)
                ) {
                    DynamicLikeButton(
                        objectUrl = likeUri,
                      Modifier
                        .offset(x = (-8).dp)
                        .size(42.dp)
                        .align(Alignment.CenterStart)
                    )
                    DynamicPlayButton(
                        command = playCommand,
                      Modifier
                        .offset(x = 8.dp)
                        .size(42.dp)
                        .align(Alignment.CenterEnd)
                    )
                }
            }
        }
    }
}