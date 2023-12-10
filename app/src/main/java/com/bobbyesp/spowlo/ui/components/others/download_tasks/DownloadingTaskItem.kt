package com.bobbyesp.spowlo.ui.components.others.download_tasks

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.bobbyesp.spowlo.features.downloader.domain.DownloadTask
import com.bobbyesp.spowlo.features.downloader.domain.TaskState
import com.bobbyesp.spowlo.ui.common.LocalDarkTheme
import com.bobbyesp.spowlo.ui.components.images.AsyncImageImpl
import com.bobbyesp.spowlo.ui.components.text.MarqueeText
import com.bobbyesp.spowlo.ui.theme.harmonizeWith
import com.bobbyesp.spowlo.ui.theme.harmonizeWithPrimary
import com.kyant.monet.LocalTonalPalettes
import com.kyant.monet.TonalPalettes.Companion.toTonalPalettes
import com.kyant.monet.dynamicColorScheme
import com.skydoves.orbital.Orbital
import com.skydoves.orbital.animateBounds
import com.skydoves.orbital.rememberMovableContentOf

val greenTonalPalettes = Color.Green.toTonalPalettes()

@Composable
fun DownloadingTaskItem(
    modifier: Modifier = Modifier,
    taskItem: DownloadTask,
    status: TaskState,
) {
    CompositionLocalProvider(LocalTonalPalettes provides greenTonalPalettes) {
        var expanded by rememberSaveable { mutableStateOf(false) }

        val greenScheme = dynamicColorScheme(!LocalDarkTheme.current.isDarkTheme())
        val accentColor = MaterialTheme.colorScheme.run {
            when (status) {
                TaskState.SUCCESS -> greenScheme.primary
                TaskState.RUNNING -> primary
                TaskState.FAILED -> error.harmonizeWithPrimary()
                TaskState.CANCELLED -> Color.Gray.harmonizeWithPrimary()
            }
        }
        val containerColor = MaterialTheme.colorScheme.run {
            surfaceColorAtElevation(3.dp).harmonizeWith(other = accentColor)
        }.copy(alpha = 0.9f)
        val contentColor = MaterialTheme.colorScheme.run {
            onSurfaceVariant.harmonizeWith(other = accentColor)
        }

        Orbital(
            modifier = modifier
                .clickable {
                    expanded = !expanded
                }
                .padding(6.dp),
        ) {
            val taskInfo = rememberMovableContentOf {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .animateBounds(
                            positionAnimationSpec = spring(stiffness = Spring.StiffnessLow),
                        ), verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    MarqueeText(
                        text = taskItem.title,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                    MarqueeText(text = taskItem.artist, style = MaterialTheme.typography.bodySmall)
                }
            }
            val image = rememberMovableContentOf {
                AsyncImageImpl(
                    modifier = Modifier
                        .animateBounds(
                            if (expanded) {
                                Modifier.size(120.dp)
                            } else {
                                Modifier.size(80.dp)
                            },
                            spring(stiffness = Spring.StiffnessLow),
                        )
                        .clip(MaterialTheme.shapes.small),
                    model = taskItem.thumbnailUrl,
                    contentDescription = "Song thumbnail"
                )
            }
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    expanded = !expanded
                },
                elevation = CardDefaults.elevatedCardElevation(
                    defaultElevation = 4.dp
                )
            ) {
                if (expanded) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        image()
                        taskInfo()
                    }
                } else {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        image()
                        taskInfo()
                    }
                }
            }
        }
    }
}