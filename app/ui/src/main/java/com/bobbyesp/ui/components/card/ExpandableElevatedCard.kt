package com.bobbyesp.ui.components.card

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ExpandLess
import androidx.compose.material.icons.outlined.PermDeviceInformation
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun ExpandableElevatedCard(
    modifier: Modifier = Modifier,
    isExpanded: Boolean = false,
    title: String,
    subtitle: String,
    icon: ImageVector,
    content: @Composable () -> Unit,
) {
    var expanded by rememberSaveable { mutableStateOf(isExpanded) }

    val animatedDegree =
        animateFloatAsState(targetValue = if (expanded) 0f else -180f, label = "Button Rotation")

    ElevatedCard(
        modifier = modifier,
        onClick = { expanded = !expanded },
        shape = MaterialTheme.shapes.small
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                modifier = Modifier.weight(0.1f),
                imageVector = icon,
                contentDescription = "Device information"
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(6.dp)
                    .weight(1f),
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.62f),
                    fontWeight = FontWeight.Normal
                )
            }
            FilledTonalIconButton(
                modifier = Modifier
                    .padding()
                    .size(24.dp),
                onClick = { expanded = !expanded }) {
                Icon(
                    Icons.Outlined.ExpandLess,
                    null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.rotate(animatedDegree.value)
                )
            }
        }
        AnimatedVisibility(visible = expanded) {
            content()
        }
    }
}

@Composable
@Preview
@Preview(uiMode = UI_MODE_NIGHT_YES)
private fun ExpandableElevatedCardPreview() {
    ExpandableElevatedCard(
        title = "Title",
        subtitle = "Subtitle",
        content = {
            Text(text = "Content")
        },
        icon = Icons.Outlined.PermDeviceInformation
    )
}