package com.bobbyesp.ui.components.chip

import android.graphics.drawable.Drawable
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.ElevatedAssistChip
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
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
    iconModifier: Modifier = Modifier,
    tint: Color = MaterialTheme.colorScheme.primary,
    onClick: () -> Unit
) {
    AssistChip(
        modifier = modifier, onClick = onClick, leadingIcon = {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = iconModifier.size(AssistChipDefaults.IconSize),
                tint = tint
            )
        }, label = { Text(text = label) })

}

@Composable
fun OutlinedButtonChip(
    modifier: Modifier = Modifier,
    icon: Drawable?,
    label: String,
    iconModifier: Modifier = Modifier,
    tint: Color = MaterialTheme.colorScheme.primary,
    onClick: () -> Unit
) {
    AssistChip(
        modifier = modifier, onClick = onClick,
        leadingIcon = {
            if (icon != null) {
                Icon(
                    painter = painterResource(id = icon.hashCode()),
                    contentDescription = null,
                    modifier = iconModifier.size(AssistChipDefaults.IconSize),
                    tint = tint
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Public,
                    contentDescription = null,
                    modifier = iconModifier.size(AssistChipDefaults.IconSize),
                    tint = tint
                )
            }
        }, label = { Text(text = label) })

}

@Composable
fun OutlinedButtonChipWithIndex(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    label: String,
    iconModifier: Modifier = Modifier,
    tint: Color = MaterialTheme.colorScheme.primary,
    index: Int?,
    onClick: () -> Unit
) {
    AssistChip(
        modifier = modifier, onClick = onClick,
        leadingIcon = {
            Box(
                modifier = Modifier,
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = iconModifier
                        .size(AssistChipDefaults.IconSize)
                        .align(Alignment.Center),
                    tint = tint
                )
                if (index != null) {
                    Text(
                        text = (index + 1).toString(),
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp))
                            .align(Alignment.BottomEnd)
                            .offset(x = 2.dp, y = 3.dp)
                    )
                }
            }
        }, label = { Text(text = label) }
    )
}

@Composable
fun SingleChoiceChip(
    modifier: Modifier = Modifier,
    selected: Boolean,
    onClick: () -> Unit,
    label: String,
    leadingIcon: ImageVector = Icons.Outlined.Check
) {
    FilterChip(
        modifier = modifier.padding(horizontal = 4.dp),
        selected = selected,
        onClick = onClick,
        label = {
            Text(text = label)
        },
        leadingIcon = {
            Row {
                AnimatedVisibility(visible = selected, modifier = Modifier) {
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