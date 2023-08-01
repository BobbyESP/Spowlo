package com.bobbyesp.spowlo.ui.components.bottombar

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.bobbyesp.spowlo.ui.common.Route

@Composable
fun RowScope.AnimatedNavigationItem( //TODO
    isSelected: Boolean,
    route: Route,
    onClick: () -> Unit
) {
    NavigationBarItem(
        selected = isSelected,
        onClick = onClick,
        icon = {
            Icon(
                imageVector = route.icon ?: return@NavigationBarItem,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface
            )
        }, label = {
            Text(
                text = route.title,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
    )
}