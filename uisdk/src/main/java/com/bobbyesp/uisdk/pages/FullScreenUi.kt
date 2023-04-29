package com.bobbyesp.uisdk.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.BugReport
import androidx.compose.material.icons.rounded.Error
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bobbyesp.uisdk.FloatingWindowInsetsAsPaddings
import com.bobbyesp.uisdk.R

@Composable
fun FullscreenLoading() {
    Box(Modifier.fillMaxSize().padding(FloatingWindowInsetsAsPaddings)) {
        CircularProgressIndicator(modifier = Modifier
            .align(Alignment.Center)
            .size(56.dp))
    }
}

@Composable
fun FullscreenError(
    onReload: () -> Unit,
    exception: Throwable
) {
    val clipboard = LocalClipboardManager.current

    Box(Modifier.fillMaxSize().padding(FloatingWindowInsetsAsPaddings)) {
        Column(
            Modifier
                .align(Alignment.Center),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                Icons.Rounded.Error, contentDescription = null, modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .size(56.dp)
            )

            Text(
                stringResource(id = R.string.err_text),
            )

            TextButton(
                onClick = { onReload() }) {
                Text(stringResource(id = R.string.err_act_reload))
            }
        }

        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp)
        ) {
            TextButton(
                onClick = {
                    clipboard.setText(AnnotatedString("Message: ${exception.message}\n\n" + exception.stackTraceToString()))
                }) {
                Icon(imageVector = Icons.Rounded.BugReport, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(stringResource(id = R.string.err_act_copy))
            }
        }
    }
}

@Composable
fun FullscreenPlaceholder(
    icon: ImageVector,
    title: String,
    text: String
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            icon, contentDescription = null, modifier = Modifier
                .size(56.dp)
                .padding(bottom = 12.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(title, fontSize = 21.sp, color = MaterialTheme.colorScheme.onSurface)

        Spacer(modifier = Modifier.height(4.dp))

        Text(text, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp))
    }
}