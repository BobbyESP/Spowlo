package com.bobbyesp.spowlo.ui.components.buttons

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bobbyesp.spowlo.R
import com.bobbyesp.spowlo.ui.theme.SpowloTheme
import com.bobbyesp.spowlo.ui.theme.harmonizeWithPrimary

@Composable
fun FilledButtonWithIcon(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    icon: ImageVector,
    enabled : Boolean = true,
    text: String
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
            contentDescription = null
        )
        Text(
            modifier = Modifier.padding(start = 6.dp),
            text = text
        )
    }
}


@Composable
fun OpenInSpotifyFilledButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    FilledTonalButton(
        modifier = modifier,
        onClick = onClick,
        colors = ButtonDefaults.filledTonalButtonColors(
            containerColor = Color(red = 30, green = 215, blue = 96).harmonizeWithPrimary(),
        ),
        contentPadding = ButtonDefaults.ButtonWithIconContentPadding
    )
    {
        Icon(
            modifier = Modifier.size(18.dp),
            imageVector = ImageVector.vectorResource(id = R.drawable.spotify_logo),
            tint = MaterialTheme.colorScheme.surfaceTint,
            contentDescription = "Spotify logo"
        )
        Text(
            modifier = Modifier.padding(start = 8.dp),
            text = stringResource(id = R.string.open_in_spotify),
            style = MaterialTheme.typography.labelMedium
        )
    }
}

@Composable
fun FilledTonalButtonWithIcon(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    icon: ImageVector,
    text: String
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
            contentDescription = null
        )
        Text(
            modifier = Modifier.padding(start = 8.dp),
            text = text
        )
    }
}

@Composable
fun ListenOnSpotifyFilledButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    FilledTonalButton(
        modifier = modifier,
        onClick = onClick,
        contentPadding = ButtonDefaults.ButtonWithIconContentPadding
    )
    {
        Icon(
            modifier = Modifier.size(18.dp),
            imageVector = ImageVector.vectorResource(id = R.drawable.spotify_logo),
            tint = MaterialTheme.colorScheme.surfaceTint,
            contentDescription = "Spotify logo"
        )
        Text(
            modifier = Modifier.padding(start = 8.dp),
            text = stringResource(id = R.string.listen_on_spotify),
            style = MaterialTheme.typography.labelMedium
        )
    }
}

@Preview
@Preview(uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun OpenInSpotifyFilledButtonPreview() {
    SpowloTheme {
        OpenInSpotifyFilledButton(onClick = {})
    }
}

@Preview
@Preview(uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun ListenOnSpotifyFilledButtonPreview() {
    SpowloTheme {
        ListenOnSpotifyFilledButton(onClick = {})
    }
}
