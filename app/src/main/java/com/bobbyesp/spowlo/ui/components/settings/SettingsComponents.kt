package com.bobbyesp.spowlo.ui.components.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
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
    addTonalElevation: Boolean = true,
    clipCorners: Boolean = false,
    highlightIcon: Boolean = false
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
            leadingIconColor = if (highlightIcon) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
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
    addTonalElevation: Boolean = true,
    clipCorners: Boolean = false,
    highlightIcon: Boolean = false
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
    addTonalElevation: Boolean = true,
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

//settings switch with divider between the switch and the rest of the item. On click actions are independent of the switch
@Composable
fun SettingsSwitchWithDivider(
    onCheckedChange: ((Boolean) -> Unit)?,
    checked: Boolean,
    title: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    description: (@Composable () -> Unit)? = null,
    icon: ImageVector? = null,
    thumbContent: (@Composable () -> Unit)? = null,
    addTonalElevation: Boolean = true,
    clipCorners: Boolean = false,
    highlightIcon: Boolean = false,
    onClick: () -> Unit = {}
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
        onClick = { onClick() },
        trailing = {
            Row(
                modifier = Modifier,
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                Divider(
                    modifier = Modifier
                        .height(32.dp)
                        .padding(horizontal = 8.dp)
                        .width(1f.dp)
                        .align(Alignment.CenterVertically),
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                )
                Switch(
                    checked = checked,
                    onCheckedChange = onCheckedChange,
                    enabled = enabled,
                    thumbContent = thumbContent
                )
            }
        },
        addTonalElevation = addTonalElevation,
        clipCorners = clipCorners,
        highlightIcon = highlightIcon
    )
}

@Composable
fun ElevatedSettingsCard(
    content: @Composable () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(
                3.dp
            )
        )
    ) {
        content()
    }
}