package com.bobbyesp.spowlo.ui.components.others

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bobbyesp.spowlo.ui.components.text.AutoResizableText
import com.bobbyesp.spowlo.ui.theme.SpowloTheme

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
        Box(
            modifier = Modifier
                .fillMaxWidth()
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
@Preview(uiMode = UI_MODE_NIGHT_YES)
@Composable
fun ConsoleOutputComponentPreview() {
    SpowloTheme {
        ConsoleOutputComponent(modifier = Modifier.width(300.dp))
    }
}
