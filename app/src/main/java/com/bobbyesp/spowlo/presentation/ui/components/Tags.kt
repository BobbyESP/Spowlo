package com.bobbyesp.spowlo.presentation.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArchTag(
    modifier: Modifier = Modifier,
    arch: ArchType,
    onClick: () -> Unit = {},
    shape: CornerBasedShape = MaterialTheme.shapes.extraSmall,
    isClickable: Boolean = false
) {
    Surface(modifier = modifier
        .clickable(onClick = onClick),
        shape = shape,
        color = MaterialTheme.colorScheme.secondaryContainer) {

        Row(modifier = modifier
            .padding(4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        )
        {
            Icon(
                modifier = Modifier.size(16.dp),
                imageVector = Icons.Filled.Memory,
                contentDescription = null,
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(text = arch.type,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface)
        }
    }
}

@Composable
@Preview
fun PreviewArchTag() {
    ArchTag(arch = ArchType.Arm64)
}