package com.bobbyesp.appmodules.hub.ui.components.dac.section

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.bobbyesp.uisdk.components.HorizontalDivider
import com.spotify.home.dac.component.v1.proto.SectionHeaderComponent

@Composable
fun SectionHeaderComponentBinder(
    item: SectionHeaderComponent
) {
    Card(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .padding(top = 12.dp)
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(
                3.dp
            )
        ),
        shape = MaterialTheme.shapes.medium.copy(
            topStart = MaterialTheme.shapes.medium.topStart,
            topEnd = MaterialTheme.shapes.medium.topEnd,
            bottomEnd = CornerSize(0.dp),
            bottomStart = CornerSize(0.dp)
        ),
    ) {
        Text(
            text = item.title,
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.Start),
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.headlineSmall
        )
        HorizontalDivider( modifier = Modifier)
    }
}