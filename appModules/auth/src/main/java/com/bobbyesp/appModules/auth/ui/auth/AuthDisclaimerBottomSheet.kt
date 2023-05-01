package com.bobbyesp.appModules.auth.ui.auth

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Done
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import com.bobbyesp.appModules.auth.R
import com.bobbyesp.uisdk.components.BottomSheetLayout

@Composable
fun AuthDisclaimerBottomSheet(
    onCancel: () -> Unit
) {
    BottomSheetLayout(
        title = { stringResource(id = R.string.auth_discl_title) },
        subtitle = { AnnotatedString(stringResource(id = R.string.auth_discl_desc)) },
        hasSubtitlePadding = true,
        content = {
            FilledTonalButton(
                onClick = onCancel,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                contentPadding = PaddingValues(16.dp),
                shape = MaterialTheme.shapes.medium
            ) {
                Icon(imageVector = Icons.Rounded.Done, contentDescription = "Accept icon for disclaimer")
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = stringResource(id = R.string.confirm))
            }
        }
    )
}