package com.bobbyesp.spowlo.ui.pages.utilities.tag_editor.editor.alertDialogs

import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.bobbyesp.spowlo.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MediaStoreInfoDialog(
    modifier: Modifier = Modifier,
    onDismissRequest: () -> Unit,
) {
    AlertDialog(
        icon = {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = "Info icon"
            )
        },
        modifier = modifier,
        onDismissRequest = onDismissRequest,
        title = {
            Text(text = stringResource(id = R.string.mediastore_info))
        },
        text = {
            Column {
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(
                onClick = onDismissRequest
            ) {
                Text(stringResource(id = R.string.dismiss))
            }
        }
    )
}