package com.bobbyesp.spowlo.ui.components.chips

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.ElevatedAssistChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun ButtonChip(
    modifier: Modifier = Modifier,
    label: String,
    enabled: Boolean = true,
    icon: ImageVector? = null,
    iconColor: Color = MaterialTheme.colorScheme.primary,
    onClick: () -> Unit,
) {
    ElevatedAssistChip(
        modifier = modifier.padding(horizontal = 4.dp),
        onClick = onClick,
        label = { Text(label) },
        colors = AssistChipDefaults.elevatedAssistChipColors(leadingIconContentColor = iconColor),
        enabled = enabled,
        leadingIcon = {
            if (icon != null) Icon(
                imageVector = icon, null, modifier = Modifier.size(AssistChipDefaults.IconSize)
            )
        }
    )
}


@Composable
fun FlatButtonChip(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    label: String,
    iconColor: Color = MaterialTheme.colorScheme.primary,
    labelColor: Color = MaterialTheme.colorScheme.onSurface,
    onClick: () -> Unit
) {
    AssistChip(
        modifier = modifier.padding(horizontal = 4.dp),
        colors = AssistChipDefaults.assistChipColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.78f),
            labelColor = labelColor,
            leadingIconContentColor = iconColor
        ),
        border = null,
        onClick = onClick,
        leadingIcon = {
            Icon(
                imageVector = icon,
                contentDescription = null, Modifier.size(AssistChipDefaults.IconSize)
            )
        },
        label = { Text(text = label) })
}

@Composable
fun OutlinedButtonChip(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    label: String,
    onClick: () -> Unit
) {
    AssistChip(modifier = modifier, onClick = onClick, leadingIcon = {
        Icon(
            imageVector = icon,
            contentDescription = null, Modifier.size(AssistChipDefaults.IconSize)
        )
    }, label = { Text(text = label) })

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SingleChoiceChip(
    modifier: Modifier = Modifier,
    selected: Boolean,
    onClick: () -> Unit,
    label: String,
    leadingIcon: ImageVector = Icons.Outlined.Check
) {
    FilterChip(
        modifier = modifier.padding(horizontal = 4.dp).animateContentSize(),
        selected = selected,
        onClick = onClick,
        label = {
            Text(text = label)
        },
        leadingIcon = {
            Row {
                AnimatedVisibility(visible = selected, modifier = Modifier.animateContentSize()) {
                    Icon(
                        imageVector = leadingIcon,
                        contentDescription = null,
                        modifier = Modifier.size(FilterChipDefaults.IconSize)
                    )
                }
            }
        },
    )
}