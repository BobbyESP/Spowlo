package com.bobbyesp.ui.components.others

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.bobbyesp.ui.ext.harmonizeWithPrimary

@Composable
fun SelectableSurface(
    modifier: Modifier = Modifier,
    tonalElevation: Dp = 0.dp,
    borderStroke: BorderStroke? = null,
    shape: Shape = MaterialTheme.shapes.small,
    isSelected: Boolean = false,
    onSelected: () -> Unit = {},
    content: @Composable () -> Unit
) {
    val animatedColor by animateColorAsState(
        targetValue = selectColorHandler(isSelected), label = "Animated transition for color change"
    )

    Surface(
        modifier = modifier.selectable(
            selected = isSelected,
            onClick = onSelected,
            role = null
        ),
        color = animatedColor,
        border = borderStroke,
        shape = shape,
        tonalElevation = tonalElevation,
    ) {
        content()
    }
}

@Composable
private fun selectColorHandler(
    isSelected: Boolean
): Color {
    return if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.onSecondary.harmonizeWithPrimary()
}

@Preview
@Composable
fun SelectableSurfacePreview() {
    MaterialTheme {
        var selected by remember { mutableStateOf(false) }
        SelectableSurface(
            isSelected = selected,
            onSelected = { selected = !selected }
        ) {
            Column(
                modifier = Modifier
                    .size(200.dp)
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Text(text = "SelectableSurface", fontWeight = FontWeight.Bold)

                Text(text = "This is just a test to see how this should work. Lol this is a very long text don't you think? I think so.")
            }
        }
    }
}