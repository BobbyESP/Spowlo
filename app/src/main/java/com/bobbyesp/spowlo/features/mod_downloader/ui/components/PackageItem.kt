package com.bobbyesp.spowlo.features.mod_downloader.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

enum class ArchType(
    val type: String,
    val description: String? = null
) {
    Arm64("ARM64-v8a", "64-bit ARM"),
    Arm("ARMEABI-v7a", "32-bit ARM"),
    Merged("Merged", "Merged"),
}

@Composable
fun PackageItemComponent(
    modifier: Modifier = Modifier,
    type: ArchType = ArchType.Arm64,
    link: String = "",
    onClick: () -> Unit = {},
    onArchClick: () -> Unit = {},
    version: String = "8.7.78.373",
    onLongClick: () -> Unit = {},
    onCopyClick: () -> Unit = {},
) {
    Surface(modifier = modifier.clickable(onClick = onClick).padding(6.dp)) {
        Row(
            modifier = modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ArchTag(modifier = Modifier.height(IntrinsicSize.Max).width(IntrinsicSize.Max) ,arch = type, onClick = onArchClick)
            Text(
                modifier = Modifier.padding(start = 8.dp).height(IntrinsicSize.Max).width(IntrinsicSize.Min),
                text = version,
                style = MaterialTheme.typography.bodySmall,
            )
            Box(modifier = Modifier
                .fillMaxWidth()
                .wrapContentSize(Alignment.CenterEnd)) {
                Row(
                    modifier = Modifier,
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                )
                {
                    FilledTonalIconButton(
                        modifier = Modifier
                            .padding(end = 12.dp)
                            .align(Alignment.Bottom)
                            .size(24.dp),
                        onClick = onClick
                    ) {
                        Icon(
                            Icons.Filled.Download,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier
                                .size(18.dp)
                        )
                    }
                    FilledTonalIconButton(
                        modifier = Modifier
                            .padding()
                            .align(Alignment.Bottom)
                            .size(24.dp),
                        onClick = onCopyClick
                    ) {
                        Icon(
                            Icons.Outlined.ContentCopy,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}