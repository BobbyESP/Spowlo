package com.bobbyesp.spowlo.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bobbyesp.spowlo.R
import com.bobbyesp.spowlo.ui.components.text.AutoResizableText

@Composable
fun ConsoleOutputComponent(
    modifier: Modifier = Modifier,
    consoleOutput: String = "Unknown console output",
    onClick: () -> Unit = {},
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
    ) {
        Text(
            text = stringResource(id = R.string.console_output),
            style = MaterialTheme.typography.headlineMedium.copy(
                fontSize = 18.sp,
                fontFamily = FontFamily.Monospace
            ),
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp)
                .clip(MaterialTheme.shapes.small)
                .background(Color.Black.copy(alpha = 0.8f))
                .clickable { onClick() },
        ) {
            AutoResizableText(
                text = consoleOutput,
                modifier = Modifier.padding(8.dp),
                textStyle = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace),
                // color is going to be like this: if the system color is dark, then the text color is white, otherwise it's black
                color = Color.White,
            )
        }
    }
}

@Preview
@Composable
fun ConsoleOutputComponentPreview() {
    ConsoleOutputComponent(modifier = Modifier.size(300.dp))
}

