package com.bobbyesp.spowlo.presentation.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bobbyesp.spowlo.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RelevantInfoItem(
    modifier: Modifier = Modifier,
    cpuArch: String,
    originalSpotifyVersion: String,
    clonedSpotifyVersion: String
){
    ElevatedCard(
        modifier = modifier
            .padding(12.dp),
        shape = MaterialTheme.shapes.small){

        Box{
            Column(modifier = Modifier
                .fillMaxWidth()
                .padding(6.dp)
            ) {
                Row(
                    modifier = Modifier
                        .padding(start = 8.dp, end = 8.dp, top = 4.dp, bottom = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(id = R.string.cpu_arch),
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Box(modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentSize(Alignment.CenterEnd)){
                        Text(
                            text = cpuArch,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                    }
                }
                Divider()

                Row(
                    modifier = Modifier
                        .padding(start = 8.dp, end = 8.dp, top = 4.dp, bottom = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(id = R.string.installed_spotify_version),
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Box(modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentSize(Alignment.CenterEnd)){
                        Row(modifier = Modifier) {
                            Text(
                                text = originalSpotifyVersion,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface,
                            )
                            Text(
                                text = " | ",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface,
                            )
                            Text(
                                text = clonedSpotifyVersion,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
@Preview
fun RelevantInfoItemPreview(){
    RelevantInfoItem(
        cpuArch = "ARM64-v8a",
        originalSpotifyVersion = "8.6.51.1000",
        clonedSpotifyVersion = "8.6.51.1000"
    )
}