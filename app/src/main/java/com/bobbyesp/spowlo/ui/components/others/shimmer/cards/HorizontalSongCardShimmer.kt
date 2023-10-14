package com.bobbyesp.spowlo.ui.components.others.shimmer.cards

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.bobbyesp.spowlo.ui.components.shimmerEffect

@Composable
fun HorizontalSongCardShimmer(
    modifier: Modifier = Modifier,
    showSongImage: Boolean = true
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (showSongImage) {
            Box(
                Modifier
                    .width(48.dp)
                    .height(48.dp)
                    .aspectRatio(1f)
                    .clip(MaterialTheme.shapes.extraSmall)
                    .shimmerEffect()
            )
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp)
                .padding(start = 8.dp),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(18.dp)
                    .clip(MaterialTheme.shapes.small)
                    .shimmerEffect()
            )
            Spacer(modifier = Modifier.height(6.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .height(12.dp)
                    .clip(MaterialTheme.shapes.extraSmall)
                    .shimmerEffect()
            )
        }
    }
}