package com.bobbyesp.spowlo.ui.components.songs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bobbyesp.spowlo.R

@ExperimentalMaterial3Api
@Composable
fun ExplicitIcon(visible: Boolean, modifier: Modifier = Modifier) {
    if(visible) {
        Surface(modifier = modifier,
            color = MaterialTheme.colorScheme.secondaryContainer,
        shape = MaterialTheme.shapes.extraSmall) {

            Row(modifier = Modifier,
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
            )
            {
                Text(text = "E",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(6.dp, 4.dp, 6.dp, 4.dp )
                )
            }
        }
    }
}

@ExperimentalMaterial3Api
@Composable
fun LyricsIcon(visible: Boolean, modifier : Modifier = Modifier) {
    if(visible) {
        Surface(modifier = Modifier,
            color = MaterialTheme.colorScheme.secondaryContainer,
            shape = MaterialTheme.shapes.extraSmall) {

            Row(modifier = Modifier,
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
            )
            {
                Text(text = stringResource(id = R.string.lyrics).uppercase(),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(6.dp, 4.dp, 6.dp, 4.dp)
                )
            }
        }
    }
}

@Composable
fun CustomTag(text: String){
    Surface(
        modifier = Modifier,
        color = MaterialTheme.colorScheme.secondaryContainer,
        shape = MaterialTheme.shapes.extraSmall
    ) {
        Row(
            modifier = Modifier,
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        )
        {
            Text(
                text = text,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(6.dp, 4.dp, 6.dp, 4.dp)
            )
        }
    }
}
@ExperimentalMaterial3Api
@Preview
@Composable
fun ExplicitIconPreview() {
    ExplicitIcon(visible = true, modifier = Modifier)
}

@ExperimentalMaterial3Api
@Preview
@Composable
fun LyricsIconPreview() {
    LyricsIcon(visible = true, modifier = Modifier)
}