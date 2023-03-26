package com.bobbyesp.spowlo.ui.components.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun SettingsItemNew(
    title: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    description: (@Composable () -> Unit)? = null,
    trailing: (@Composable () -> Unit)? = null,
    icon: ImageVector? = null,
    addTonalElevation: Boolean = false,
    clipCorners: Boolean = false,
    highlightIcon : Boolean = false
) {
    ListItem(
        modifier = Modifier
            .apply { if (clipCorners) this.clip(MaterialTheme.shapes.medium) }
            .then(modifier),
        leadingContent = {
            icon?.let {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                )
            }
        },
        trailingContent = trailing,
        supportingContent = description,
        headlineContent = title,
        tonalElevation = if (addTonalElevation) 3.dp else 0.dp,
        colors = ListItemDefaults.colors(
            leadingIconColor = if(highlightIcon) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
        )
    )
}

@Composable
fun SettingsItemNew(
    onClick: () -> Unit,
    title: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    description: (@Composable () -> Unit)? = null,
    trailing: (@Composable () -> Unit)? = null,
    icon: ImageVector? = null,
    addTonalElevation: Boolean = false,
    clipCorners: Boolean = false,
    highlightIcon : Boolean = false
) {
    SettingsItemNew(
        modifier = modifier
            .clickable(
                onClick = onClick,
                enabled = enabled
            )
            .alpha(if (enabled) 1f else 0.5f),
        icon = icon,
        description = description,
        title = title,
        trailing = trailing,
        addTonalElevation = addTonalElevation,
        clipCorners = clipCorners,
        highlightIcon = highlightIcon
    )
}

@Composable
fun SettingsSwitch(
    onCheckedChange: ((Boolean) -> Unit)?,
    checked: Boolean,
    title: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    description: (@Composable () -> Unit)? = null,
    icon: ImageVector? = null,
    thumbContent: (@Composable () -> Unit)? = null,
    addTonalElevation: Boolean = false,
    clipCorners: Boolean = false,
    highlightIcon: Boolean = false
) {
    val toggleableModifier = if (onCheckedChange != null) {
        Modifier.toggleable(
            value = checked,
            enabled = enabled,
            onValueChange = onCheckedChange
        ).apply { if (!enabled) this.alpha(0.5f) }
    } else Modifier

    SettingsItemNew(
        modifier = modifier
            .then(toggleableModifier),
        icon = icon,
        description = description,
        title = title,
        trailing = {
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                enabled = enabled,
                thumbContent = thumbContent
            )
        },
        addTonalElevation = addTonalElevation,
        clipCorners = clipCorners,
        highlightIcon = highlightIcon
    )
}