package com.bobbyesp.spowlo.presentation.ui.pages.downloader

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Surface
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun DownloaderPage(
    navController: NavController,
    downloaderViewModel: DownloaderViewModel = hiltViewModel()
) {
    val viewState = downloaderViewModel.stateFlow.collectAsState()
    val taskState = downloaderViewModel.taskState.collectAsState()

    val downloadLink by remember { mutableStateOf(viewState.value.url) }

    with(viewState.value) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background),
                color = MaterialTheme.colorScheme.background,
            ) {
                Column(modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top) {
                    InputUrl(
                        url = url,
                        hint = "Insert an url",
                        error = isDownloadError,
                        progress = taskState.value.progress,
                        onValueChange = {url -> downloaderViewModel.updateUrl(url)},
                    )
                    Button(onClick = {downloaderViewModel.startDownloadVideo() }) {
                        Text(text = "Download")
                    }
                    Text(text = "Link to download: $downloadLink")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InputUrl(
    url: String,
    hint: String,
    error: Boolean,
    showDownloadProgress: Boolean = false,
    progress: Float,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = url,
        isError = error,
        onValueChange = onValueChange,
        label = { Text(hint) },
        modifier = Modifier
            .padding(0f.dp, 16f.dp)
            .fillMaxWidth(), textStyle = MaterialTheme.typography.bodyLarge, maxLines = 3
    )
    AnimatedVisibility(visible = showDownloadProgress) {
        Row(
            Modifier.padding(0.dp, 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            val progressAnimationValue by animateFloatAsState(
                targetValue = progress / 100f,
                animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing)
            )
            if (progressAnimationValue < 0)
                LinearProgressIndicator(
                    modifier = Modifier
                        .weight(0.75f)
                        .clip(MaterialTheme.shapes.large),
                )
            else
                LinearProgressIndicator(
                    progress = progressAnimationValue,
                    modifier = Modifier
                        .weight(0.75f)
                        .clip(MaterialTheme.shapes.large),
                )
            Text(
                text = if (progress < 0) "0%" else "$progress%",
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(0.25f)
            )
        }
    }
}