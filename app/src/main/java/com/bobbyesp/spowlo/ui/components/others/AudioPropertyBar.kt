package com.bobbyesp.spowlo.ui.components.others

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bobbyesp.spowlo.ui.theme.SpowloTheme

@Composable
fun AudioPropertyBar(modifier: Modifier, property: String, value: Float) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(6.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = property,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
            fontWeight = FontWeight.Bold,
            modifier = Modifier
        )
        LinearProgressIndicator(
            progress = value,
            modifier = Modifier
                .fillMaxWidth()
                .clip(MaterialTheme.shapes.small)
                .height(4.dp),
            color = MaterialTheme.colorScheme.secondary
        )
    }
}

@Preview
@Preview(uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun AudioPropertyBarPrev() {
    SpowloTheme {
        AudioPropertyBar(modifier = Modifier, property = "Popularity", value = 0.72f)
    }
}