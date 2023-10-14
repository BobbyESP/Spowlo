package com.bobbyesp.spowlo.ui.pages.common_pages

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Error
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import com.bobbyesp.spowlo.R

@Composable
fun ErrorPage(
    onReload: () -> Unit,
    exception: String,
    modifier: Modifier
) {
    val clipboard = LocalClipboardManager.current

    Box(modifier) {
        Column(
            Modifier
                .align(Alignment.Center)
        ) {
            Icon(
                Icons.Rounded.Error, contentDescription = null, modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .size(56.dp)
                    .padding(bottom = 12.dp)
            )
            Text(
                stringResource(id = R.string.searching_error),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }

        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp)
        ) {
            OutlinedButton(
                onClick = {
                    clipboard.setText(AnnotatedString("Message: ${exception}\n\n"))
                }) {
                Text(stringResource(id = R.string.error_copy))
            }

            Spacer(modifier = Modifier.width(8.dp))

            OutlinedButton(
                onClick = { onReload() }) {
                Text(stringResource(id = R.string.err_act_reload))
            }
        }
    }
}