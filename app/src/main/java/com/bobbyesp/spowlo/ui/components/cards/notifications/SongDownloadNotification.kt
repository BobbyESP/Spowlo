package com.bobbyesp.spowlo.ui.components.cards.notifications

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bobbyesp.spowlo.features.inapp_notifications.domain.model.Notification
import com.bobbyesp.spowlo.ui.components.images.AsyncImageImpl
import kotlinx.coroutines.delay

@Composable
fun SongDownloadNotification(
    modifier: Modifier = Modifier,
    notification: Notification,
    showBar: Boolean = true
) {
    Surface(
        modifier = modifier,
        tonalElevation = 6.dp,
        shape = MaterialTheme.shapes.small
    ) {
        Row(
            modifier = Modifier
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            notification.entityInfo?.artworkUrl?.let { artworkUrl ->
                AsyncImageImpl(
                    modifier = Modifier
                        .clip(MaterialTheme.shapes.extraSmall)
                        .size(52.dp)
                        .padding(end = 8.dp),
                    model = artworkUrl,
                    contentDescription = "Song Artwork"
                )
            }
            Column {
                Text(
                    text = notification.title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = notification.subtitle,
                    style = MaterialTheme.typography.bodyMedium
                )
                //create a progress from 100 to 0 and animate it by calculating the percentage based on the time that the notification will be shown (4 seconds)
                if (showBar) {
                    var progress by remember { mutableFloatStateOf(1f) }

                    LaunchedEffect(key1 = true, key2 = notification) {
                        val animationTime = notification.duration.duration
                        val startTime = withFrameNanos { it }
                        while (withFrameNanos { it } - startTime < animationTime) {
                            progress =
                                ((withFrameNanos { it } - startTime) / animationTime.toFloat())
                            delay(15) // Adjust the delay to change the smoothness of the animation
                        }
                        progress = 0f
                    }
                    val animatedProgressBar by animateFloatAsState(
                        targetValue = progress,
                        animationSpec = tween(4000, easing = LinearEasing),
                        label = "progressBarAnimation"
                    )

                    LinearProgressIndicator(
                        progress = { animatedProgressBar },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
            }
        }
    }
}

@Composable
@Preview
fun SongDownloadNotificationPreview() {
    SongDownloadNotification(
        notification = Notification(
            id = 1,
            title = "Title",
            subtitle = "Subtitle",
            timestamp = System.currentTimeMillis()
        )
    )
}