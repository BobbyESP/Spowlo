package com.bobbyesp.ui.common.pages

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ErrorOutline
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bobbyesp.ui.R

@Composable
fun ErrorPage(
    modifier: Modifier = Modifier,
    error: String,
    onRetry: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Rounded.ErrorOutline,
            contentDescription = stringResource(id = R.string.error)
        )
        Text(
            text = stringResource(id = R.string.unknown_error_title),
            style = MaterialTheme.typography.displayMedium,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.padding(8.dp))
        Text(text = error)
        Button(onClick = onRetry) {
            Text(text = stringResource(id = R.string.retry))
        }
    }
}

@Preview
@Preview(uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun ErrorPagePrev() {
    MaterialTheme {
        ErrorPage(
            error = "An error occurred",
            onRetry = {}
        )
    }
}