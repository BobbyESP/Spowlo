package com.bobbyesp.spowlo.ui.components.download_tasks

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.RestartAlt
import androidx.compose.material.icons.outlined.Terminal
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.bobbyesp.spowlo.R
import com.bobbyesp.spowlo.ui.common.LocalDarkTheme
import com.bobbyesp.spowlo.ui.components.AutoResizableText
import com.bobbyesp.spowlo.ui.components.FlatButtonChip
import com.bobbyesp.spowlo.ui.components.MarqueeText
import com.bobbyesp.spowlo.ui.theme.harmonizeWith
import com.bobbyesp.spowlo.ui.theme.harmonizeWithPrimary
import com.kyant.monet.LocalTonalPalettes
import com.kyant.monet.TonalPalettes.Companion.toTonalPalettes
import com.kyant.monet.dynamicColorScheme

val greenTonalPalettes = Color.Green.toTonalPalettes()

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DownloadingTaskItem(
    modifier: Modifier = Modifier,
    status: TaskState = TaskState.ERROR,
    progress: Float = .85f,
    url: String = "https://www.example.com",
    header: String = "Faded - Alan Walker",
    progressText: String = "[sample] Extracting URL: https://www.example.com\n" +
            "[sample] sample: Downloading webpage\n" +
            "[sample] sample: Downloading android player API JSON\n" +
            "[info] Available automatic captions for sample:" + "[info] Available automatic captions for sample:",
    artworkUrl: String = "https://www.example.com",
    onCopyLog: () -> Unit = {},
    onCopyError: () -> Unit = {},
    onRestart: () -> Unit = {},
    onShowLog: () -> Unit = {},
    onCopyLink: () -> Unit = {},
) {
    CompositionLocalProvider(LocalTonalPalettes provides greenTonalPalettes) {
        val greenScheme = dynamicColorScheme(!LocalDarkTheme.current.isDarkTheme())
        val accentColor = MaterialTheme.colorScheme.run {
            when (status) {
                TaskState.FINISHED -> greenScheme.primary
                TaskState.RUNNING -> primary
                TaskState.ERROR -> error.harmonizeWithPrimary()
            }
        }
        val containerColor = MaterialTheme.colorScheme.run {
            surfaceColorAtElevation(3.dp).harmonizeWith(other = accentColor)
        }.copy(alpha = 0.9f)
        val contentColor = MaterialTheme.colorScheme.run {
            onSurfaceVariant.harmonizeWith(other = accentColor)
        }

        val labelText = stringResource(
            id = when (status) {
                TaskState.FINISHED -> R.string.status_completed
                TaskState.RUNNING -> R.string.downloading
                TaskState.ERROR -> R.string.error
            }
        )
        Surface(
            modifier = modifier,
            color = containerColor,
            shape = CardDefaults.shape,
            onClick = { onShowLog() },
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.semantics(mergeDescendants = true) { },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    when (status) {
                        TaskState.FINISHED -> {
                            Icon(
                                modifier = Modifier
                                    .padding(8.dp)
                                    .size(24.dp),
                                imageVector = Icons.Filled.CheckCircle,
                                tint = accentColor,
                                contentDescription = stringResource(id = R.string.status_completed)
                            )
                        }

                        TaskState.RUNNING -> {
                            val animatedProgress by animateFloatAsState(
                                targetValue = progress,
                                animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec,
                                label = ""
                            )
                            if (progress < 0)
                                CircularProgressIndicator(
                                    modifier = Modifier
                                        .padding(8.dp)
                                        .size(24.dp),
                                    strokeWidth = 5.dp, color = accentColor
                                )
                            else
                                CircularProgressIndicator(
                                    modifier = Modifier
                                        .padding(8.dp)
                                        .size(24.dp),
                                    strokeWidth = 5.dp,
                                    progress = animatedProgress,
                                    color = accentColor
                                )
                        }

                        TaskState.ERROR -> {
                            Icon(
                                modifier = Modifier
                                    .padding(8.dp)
                                    .size(24.dp),
                                imageVector = Icons.Filled.Error,
                                tint = accentColor,
                                contentDescription = stringResource(id = R.string.searching_error)
                            )
                        }
                    }

                    Column(
                        Modifier
                            .padding(horizontal = 8.dp)
                            .weight(1f)
                    ) {
                        MarqueeText(
                            text = header,
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            fontWeight = FontWeight.Bold,

                        )
                        Text(
                            text = url,
                            style = MaterialTheme.typography.bodyMedium,
                            maxLines = 1,
                            color = contentColor,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    IconButton(
                        modifier = Modifier
                            .align(Alignment.Top)
                            .semantics(mergeDescendants = true) { },
                        onClick = { onShowLog() },
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = MaterialTheme.colorScheme.surface,
                            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Terminal,
                            contentDescription = stringResource(
                                id = R.string.open_log
                            )
                        )
                    }
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .padding(top = 4.dp)
                        .clip(MaterialTheme.shapes.small)
                        .background(Color.Black.copy(alpha = 0.8f)),
                ) {
                    AutoResizableText(
                        text = progressText,
                        modifier = Modifier.padding(8.dp),
                        textStyle = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace),
                        // color is going to be like this: if the system color is dark, then the text color is white, otherwise it's black
                        color = Color.White,
                        maxLines = 1
                    )
                }

                Row(modifier = Modifier.horizontalScroll(rememberScrollState())) {
                    FlatButtonChip(
                        icon = Icons.Outlined.ContentCopy,
                        label = stringResource(id = R.string.copy_log)
                    ) { onCopyLog() }
                    FlatButtonChip(
                        icon = Icons.Outlined.ContentCopy,
                        label = stringResource(id = R.string.copy_link)
                    ) { onCopyLink() }
                    if (status == TaskState.ERROR) {
                        FlatButtonChip(
                            icon = Icons.Outlined.ErrorOutline,
                            label = stringResource(id = R.string.copy_error_report),
                            iconColor = MaterialTheme.colorScheme.error,
                        ) { onCopyError() }
                        FlatButtonChip(
                            icon = Icons.Outlined.RestartAlt,
                            label = stringResource(id = R.string.restart_task),
                            iconColor = MaterialTheme.colorScheme.secondary,
                        ) { onRestart() }
                    }
                }
            }
        }
    }
}

enum class TaskState {
    FINISHED, RUNNING, ERROR
}