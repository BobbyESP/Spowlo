package com.bobbyesp.appmodules.hub.ui.components.dac.actionCards

import android.graphics.Color
import android.os.Build
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.unit.dp
import com.bobbyesp.appmodules.hub.ui.components.DynamicLikeButton
import com.bobbyesp.appmodules.hub.ui.components.DynamicPlayButton
import com.bobbyesp.appmodules.hub.ui.components.PlaceholderType
import com.bobbyesp.appmodules.hub.ui.components.PreviewableAsyncImage
import com.bobbyesp.uisdk.components.text.MediumText
import com.bobbyesp.uisdk.components.text.Subtext
import com.bobbyesp.uisdk.monet.ColorToScheme
import com.spotify.dac.player.v1.proto.PlayCommand

@Composable
fun MediumActionCardBinder(
    title: String,
    subtitle: String,
    contentType: String,
    fact: String,
    gradientColor: String,
    likeUri: String,
    imageUri: String,
    imagePlaceholder: PlaceholderType,
    navigateUri: String,
    playCommand: PlayCommand,
    onNavigateToUri: (String) -> Unit,
) {
    val curScheme = MaterialTheme.colorScheme
    val isDark = isSystemInDarkTheme()
    var colorScheme by remember { mutableStateOf(curScheme) }

    LaunchedEffect(gradientColor) {
        val clr = Color.parseColor("#$gradientColor")
        colorScheme = ColorToScheme.convert(clr, isDark)
    }

    MaterialTheme(
        colorScheme = colorScheme
    ) {
        OutlinedCard(colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(
                3.dp
            )
        ), modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .clickable {
                onNavigateToUri(navigateUri)
            }) {
            Box(
                contentAlignment = Alignment.Center,
            ) {
                PreviewableAsyncImage(
                    imageUrl = imageUri,
                    placeholderType = imagePlaceholder,
                    modifier = Modifier
                        .fillMaxHeight()
                        .size(180.dp)
                        .run {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                                blur(16.dp, edgeTreatment = BlurredEdgeTreatment.Unbounded)
                            } else this
                        }
                )
                Column(Modifier.padding(16.dp)) {
                    Row {
                        PreviewableAsyncImage(
                            imageUrl = imageUri,
                            placeholderType = imagePlaceholder,
                            modifier = Modifier
                                .fillMaxHeight()
                                .size(140.dp)
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Column {
                            Subtext(text = contentType)
                            MediumText(text = title, maxLines = 2)
                            Spacer(modifier = Modifier.height(8.dp))
                            Subtext(text = subtitle)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        DynamicLikeButton(
                            objectUrl = likeUri, Modifier.size(42.dp)
                        )

                        Spacer(Modifier.weight(1f))

                        Subtext(text = fact)

                        Spacer(modifier = Modifier.width(16.dp))

                        DynamicPlayButton(
                            command = playCommand, Modifier.size(42.dp)
                        )
                    }
                }
            }
        }
    }
}