package com.bobbyesp.spowlo.ui.components.others.tags

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.bobbyesp.spowlo.utils.GeneralTextUtils

@Composable
fun MetadataTag(
    modifier : Modifier = Modifier,
    typeOfMetadata: String,
    metadata: String = "Unknown",
) {
    val context = LocalContext.current

    Column(
        modifier = modifier
    ) {
        Text(
            text = typeOfMetadata,
            modifier = Modifier.alpha(alpha = 0.8f),
            style = MaterialTheme.typography.labelSmall,
            textAlign = TextAlign.Start
        )
        Text(
            modifier = Modifier
                .clickable { GeneralTextUtils.copyToClipboardAndNotify(context, metadata) },
            text = metadata,
            style = MaterialTheme.typography.titleLarge.copy(fontSize = 16.sp),
            textAlign = TextAlign.Start
        )
    }
}