package com.bobbyesp.uisdk.components.topBar

import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector

@Composable
fun ToolbarItem(
    icon: ImageVector,
    uriToNavigate: String,
    contentDesc: String,
    onNavigateTo: (String) -> Unit,
) {

    IconButton(onClick = {
        onNavigateTo(uriToNavigate)
    }) {
        Icon(icon, contentDescription = contentDesc)
    }
}