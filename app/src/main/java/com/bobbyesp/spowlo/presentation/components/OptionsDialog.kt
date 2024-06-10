package com.bobbyesp.spowlo.presentation.components

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bobbyesp.spowlo.R
import com.bobbyesp.spowlo.presentation.theme.SpowloTheme
import com.bobbyesp.spowlo.presentation.theme.unbounded

@Composable
fun OptionsDialog(
    modifier: Modifier = Modifier,
    isPreview: Boolean = false,
    onExit: () -> Unit = {}
) {
    Surface(
        modifier = modifier
            .fillMaxWidth(),
        tonalElevation = 12.dp,
        shape = MaterialTheme.shapes.medium,
        shadowElevation = 8.dp
    ) {
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            IconButton(
                modifier = Modifier
                    .padding(8.dp)
                    .align(Alignment.CenterStart),
                onClick = {
                    onExit()
                }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                    contentDescription = stringResource(
                        id = R.string.go_back
                    )
                )
            }
            Text(
                text = stringResource(id = R.string.app_name),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                style = MaterialTheme.typography.headlineLarge,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.SemiBold,
                fontFamily = if (isPreview) null else unbounded
            )
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Preview
@Preview(uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun OptionsDialogPreview() {
    SpowloTheme {
        OptionsDialog(isPreview = true)
    }
}