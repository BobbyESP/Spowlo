package com.bobbyesp.ui.components.dropdown

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun DropdownItemContainer(
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit,
    contentPadding: androidx.compose.ui.unit.Dp = 0.dp
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            // Preferred min and max width used during the intrinsic measurement.
            .sizeIn(
                minWidth = DropdownMenuItemDefaultMinWidth,
                maxWidth = DropdownMenuItemDefaultMaxWidth,
                minHeight = ListItemContainerHeight
            )
            .padding(contentPadding),
        verticalAlignment = Alignment.CenterVertically
    ) {
        content()
    }
}

private val DropdownMenuItemHorizontalPadding = 12.dp
private val DropdownMenuItemDefaultMinWidth = 112.dp
private val DropdownMenuItemDefaultMaxWidth = 280.dp
private val ListItemContainerHeight = 48.0.dp