package com.bobbyesp.spowlo.ui.components.others.download_tasks

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.bobbyesp.spowlo.features.downloader.domain.DownloadTask
import com.bobbyesp.spowlo.features.downloader.domain.TaskState
import com.bobbyesp.spowlo.ui.common.LocalDarkTheme
import com.bobbyesp.spowlo.ui.theme.harmonizeWith
import com.bobbyesp.spowlo.ui.theme.harmonizeWithPrimary
import com.kyant.monet.LocalTonalPalettes
import com.kyant.monet.TonalPalettes.Companion.toTonalPalettes
import com.kyant.monet.dynamicColorScheme

val greenTonalPalettes = Color.Green.toTonalPalettes()

@Composable
fun DownloadingTaskItem(
    modifier: Modifier = Modifier,
    taskItem: DownloadTask,
    status: TaskState,
    onClick: (taskId: Int) -> Unit = {}
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

        Surface(onClick = { onClick(taskItem.hashCode()) }) {
            Text(text = taskItem.taskName, color = contentColor)
        }
    }
}