package com.bobbyesp.ui.components.button

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun FilledButtonWithIcon(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    icon: ImageVector,
    enabled: Boolean = true,
    text: String,
    contentDescription: String? = null
) {
    Button(
        modifier = modifier,
        onClick = onClick,
        enabled = enabled,
        contentPadding = ButtonDefaults.ButtonWithIconContentPadding
    )
    {
        Icon(
            modifier = Modifier.size(18.dp),
            imageVector = icon,
            contentDescription = contentDescription
        )
        Text(
            modifier = Modifier.padding(start = 6.dp),
            text = text
        )
    }
}

@Composable
fun FilledTonalButtonWithIcon(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    icon: ImageVector,
    text: String,
    contentDescription: String? = null
) {
    FilledTonalButton(
        modifier = modifier,
        onClick = onClick,
        contentPadding = ButtonDefaults.ButtonWithIconContentPadding
    )
    {
        Icon(
            modifier = Modifier.size(18.dp),
            imageVector = icon,
            contentDescription = contentDescription
        )
        Text(
            modifier = Modifier.padding(start = 8.dp),
            text = text
        )
    }
}
