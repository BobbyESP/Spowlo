package com.bobbyesp.spowlo.ui.components.buttons

import android.content.res.Configuration
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bobbyesp.spowlo.R

@Composable
fun LoginWithSpotify(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
) {
    FilledTonalButton(
        modifier = modifier,
        onClick = onClick,
        contentPadding = PaddingValues(16.dp),
        shape = MaterialTheme.shapes.small
    ) {
        Icon(
            modifier = Modifier.size(24.dp),
            imageVector = ImageVector.vectorResource(R.drawable.spotify_logo),
            contentDescription = "Spotify logo"
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = stringResource(R.string.login_with_spotify),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun LoginButtonPrev() {
    LoginWithSpotify()
}
