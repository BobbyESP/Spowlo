package com.bobbyesp.ui.components.others

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.bobbyesp.ui.R

@Composable
fun MetadataTag(
    modifier: Modifier = Modifier,
    typeOfMetadata: String,
    metadata: String = "Unknown",
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    Column(
        modifier = modifier.clickable {
            clipboardManager.setText(AnnotatedString(metadata))
            Toast.makeText(context, R.string.copied_to_clipboard, Toast.LENGTH_SHORT).show()
        }
    ) {
        Text(
            text = typeOfMetadata,
            modifier = Modifier.alpha(alpha = 0.8f),
            style = MaterialTheme.typography.labelSmall,
            textAlign = TextAlign.Start
        )
        Text(
            modifier = Modifier,
            text = metadata,
            style = MaterialTheme.typography.titleLarge.copy(fontSize = 16.sp),
            textAlign = TextAlign.Start
        )
    }
}