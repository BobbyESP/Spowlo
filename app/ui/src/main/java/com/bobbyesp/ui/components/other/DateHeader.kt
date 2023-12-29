package com.bobbyesp.ui.components.other

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun DateHeader(
    date: String,
) {
    Text(
        text = date,
        modifier = Modifier
            .fillMaxWidth()
            .offset(x = (-4).dp),
        style = MaterialTheme.typography.displaySmall,
        fontWeight = FontWeight.Medium
    )
}