package com.bobbyesp.spowlo.ui.pages.utilities.tag_editor.editor

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bobbyesp.spowlo.data.local.model.SelectedSong
import com.bobbyesp.spowlo.ui.common.LocalNavController
import com.bobbyesp.spowlo.ui.components.buttons.CloseButton
import com.bobbyesp.spowlo.ui.components.text.MarqueeText
import com.bobbyesp.spowlo.ui.components.topbars.SmallTopAppBar
import com.kyant.tag.Metadata
import com.kyant.tag.Tags.Companion.toTags

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ID3MetadataEditorPage(
    viewModel: ID3MetadataEditorPageViewModel,
    selectedSong: SelectedSong
) {
    val viewState = viewModel.pageViewState.collectAsStateWithLifecycle().value
    val pageStage = viewState.state

    val navController = LocalNavController.current

    LaunchedEffect(true) {
        viewModel.loadTrackMetadata(selectedSong.localSongPath!!)
    }

    Scaffold(
        topBar = {
            SmallTopAppBar(
                navigationIcon = {
                    CloseButton {
                        navController.popBackStack()
                    }
                }, actions = {

                }, title = {
                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        MarqueeText(
                            text = selectedSong.name,
                            style = MaterialTheme.typography.bodyLarge,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        MarqueeText(
                            text = selectedSong.mainArtist,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            ),
                            fontWeight = FontWeight.Normal
                        )
                    }
                })
        }, modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        Crossfade(
            targetState = pageStage,
            animationSpec = tween(175),
            label = "Fade between pages (ID3MetadataEditorPage)",
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) { actualPageState ->
            when (actualPageState) {
                is ID3MetadataEditorPageState.Loading -> {
                    CircularProgressIndicator()
                }

                is ID3MetadataEditorPageState.Success -> {
                    Text(text = actualPageState.metadata.toTags().toString())
                }

                is ID3MetadataEditorPageState.Error -> {
                    Text(text = actualPageState.throwable.message ?: "Unknown error")
                }
            }
        }
    }
}

@Composable
fun EditMetadataPage(
    modifier: Modifier = Modifier,
    metadata: Metadata,
    onSaveNewMetadata: (Metadata) -> Unit,
    onDiscardChanges: () -> Unit
) {

}