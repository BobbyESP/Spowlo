package com.bobbyesp.ui.components.cards

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.AirplaneTicket
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bobbyesp.ui.components.text.MarqueeText

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppUtilityCard(
    modifier: Modifier = Modifier,
    cardSize: Int = 200,
    utilityName: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    val xOffset = cardSize.dp / 2.3f
    val yOffset = cardSize.dp / 5

    OutlinedCard(
        modifier = modifier
            .aspectRatio(1.0f)
            .background(MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp)),
        onClick = onClick,
        shape = MaterialTheme.shapes.small,
    ) {
        Box(
            modifier = Modifier
        ) {
            Icon(
                imageVector = icon,
                contentDescription = "Utility Icon",
                modifier = Modifier
                    .fillMaxSize(0.8f)
                    .offset(xOffset, yOffset),
                tint = MaterialTheme.colorScheme.onSurface
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                MaterialTheme.colorScheme.surface,
                            ), startY = -200f
                        )
                    ), contentAlignment = Alignment.BottomEnd
            ) {
                MarqueeText(
                    modifier = Modifier.padding(12.dp),
                    text = utilityName,
                    fontWeight = FontWeight.Bold,
                    fontSize = MaterialTheme.typography.headlineSmall.fontSize
                )
            }
        }
    }
}

@Preview
@Preview(uiMode = UI_MODE_NIGHT_YES)
@Composable
fun AppUtilityCardPreview() {
    AppUtilityCard(
        modifier = Modifier.size(200.dp),
        onClick = {},
        icon = Icons.AutoMirrored.Outlined.AirplaneTicket,
        utilityName = "Flights"
    )
}
