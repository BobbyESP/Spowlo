package com.bobbyesp.spowlo.ui.components.text

import androidx.compose.foundation.clickable
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow

@Composable
fun ExpandableText(modifier: Modifier = Modifier, text: String, maxLines: Int) {
    var expanded by remember { mutableStateOf(false) }
    var canBeExpanded by remember { mutableStateOf(false) }

    Text(
        modifier = if(canBeExpanded) modifier.clickable { expanded = !expanded } else modifier,
        text = text,
        maxLines = if (expanded) Int.MAX_VALUE else maxLines,
        textAlign = TextAlign.Justify,
        overflow = TextOverflow.Ellipsis,
        color = MaterialTheme.colorScheme.secondary,
        fontWeight = FontWeight.Medium,
        onTextLayout = {
            if(!canBeExpanded) {
                canBeExpanded = it.hasVisualOverflow
            }
        }
    )
}
