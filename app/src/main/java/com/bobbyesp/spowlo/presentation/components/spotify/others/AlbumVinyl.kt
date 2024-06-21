package com.bobbyesp.spowlo.presentation.components.spotify.others

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlin.math.min

@Composable
fun DrawVinyl(
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val canvasWidth = size.width
        val canvasHeight = size.height

        // Calculate the size of the vinyl record
        val diameter = min(canvasWidth, canvasHeight) * 0.8f
        val radius = diameter / 2

        // Draw the outer circle of the vinyl
        drawCircle(
            color = Color.Black,
            radius = radius,
            center = center
        )

        // Draw the inner circle of the vinyl
        drawCircle(
            color = Color.DarkGray,
            radius = radius * 0.1f,
            center = center
        )

        // Optional: Draw grooves on the vinyl
        for (i in 1..10) {
            drawCircle(
                color = Color.Gray,
                radius = radius * (0.1f + i * 0.08f),
                center = center,
                style = Stroke(
                    width = 1.dp.toPx()
                )
            )
        }
        
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    DrawVinyl(Modifier.fillMaxSize())
}