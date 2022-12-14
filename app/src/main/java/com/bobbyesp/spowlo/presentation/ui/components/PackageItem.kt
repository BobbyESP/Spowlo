package com.bobbyesp.spowlo.presentation.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

enum class ArchType(
    val type: String,
    val description: String? = null
) {
    Arm64("ARM64-v8a", "64-bit ARM"),
    Arm("ARMEABI-v7a", "32-bit ARM"),
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PackageItem(
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
                            //Icons.Outlined.MoreHoriz,
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

@Composable
@Preview
fun PackageItemPreview() {
   PackageItem()
}