package com.bobbyesp.spowlo.ui.pages.utilities.tag_editor.editor

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lyrics
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import coil.compose.AsyncImagePainter
import com.bobbyesp.spowlo.R
import com.bobbyesp.spowlo.data.local.model.SelectedSong
import com.bobbyesp.spowlo.ui.common.LocalNavController
import com.bobbyesp.spowlo.ui.components.buttons.CloseButton
import com.bobbyesp.spowlo.ui.components.cards.CardListItem
import com.bobbyesp.spowlo.ui.components.images.AsyncImageImpl
import com.bobbyesp.spowlo.ui.components.images.PlaceholderCreator
import com.bobbyesp.spowlo.ui.components.others.tags.MetadataTag
import com.bobbyesp.spowlo.ui.components.text.CategoryTitle
import com.bobbyesp.spowlo.ui.components.text.MarqueeText
import com.bobbyesp.spowlo.ui.components.text.PreConfiguredOutlinedTextField
import com.bobbyesp.spowlo.ui.components.topbars.SmallTopAppBar
import com.bobbyesp.spowlo.ui.ext.joinOrNullToString
import com.bobbyesp.spowlo.ui.ext.toMinutes
import com.bobbyesp.spowlo.ui.pages.utilities.tag_editor.editor.ID3MetadataEditorPageViewModel.Companion
import com.bobbyesp.spowlo.ui.pages.utilities.tag_editor.editor.alertDialogs.MediaStoreInfoDialog
import com.kyant.tag.Metadata
import com.kyant.tag.Tags
import com.kyant.tag.Tags.Companion.toTags
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ID3MetadataEditorPage(
    viewModel: ID3MetadataEditorPageViewModel,
    selectedSong: SelectedSong
) {
    val viewState = viewModel.pageViewState.collectAsStateWithLifecycle().value
    val pageStage = viewState.state
    val navController = LocalNavController.current
    val viewModelScope = viewModel.viewModelScope

    LaunchedEffect(true) {
        viewModelScope.launch(Dispatchers.IO) {
            viewModel.loadTrackMetadata(selectedSong.localSongPath!!)
        }
    }

    Scaffold(
        topBar = {
            SmallTopAppBar(
                navigationIcon = {
                    CloseButton {
                        navController.popBackStack()
                    }
                }, actions = {
                    TextButton(onClick = { }) {
                        Text(text = stringResource(id = R.string.save))
                    }
                }, title = {
                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        MarqueeText(
                            text = stringResource(id = R.string.editing_metadata),
                            style = MaterialTheme.typography.bodyLarge,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
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
                is Companion.ID3MetadataEditorPageState.Loading -> {
                    CircularProgressIndicator()
                }

                is Companion.ID3MetadataEditorPageState.Success -> {
                    var metadataCopyState by remember {
                        mutableStateOf(
                            actualPageState.metadata.toTags().copy()
                        )
                    }

                    EditMetadataPage(
                        modifier = Modifier.fillMaxSize(),
                        metadataCopy = actualPageState.metadata.toTags(),
                        metadata = actualPageState.metadata,
                        selectedSong = selectedSong,
                    ) { updatedMetadata ->
                        viewModel.viewModelScope.launch(Dispatchers.IO) {
                            metadataCopyState = updatedMetadata
                        }
                    }
                }

                is Companion.ID3MetadataEditorPageState.Error -> {
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
    metadataCopy: Tags,
    selectedSong: SelectedSong,
    onSaveNewMetadata: (Tags) -> Unit,
) {
    val artworkUri = selectedSong.artworkPath
    var showArtwork by remember { mutableStateOf(true) }
    var showMediaStoreInfoDialog by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp)
    ) {
        Box(
            modifier = Modifier
                .size(250.dp)
                .padding(8.dp)
                .padding(bottom = 8.dp)
                .aspectRatio(1f)
                .align(Alignment.CenterHorizontally),
        ) {
            if (artworkUri != null && showArtwork) {
                AsyncImageImpl(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(MaterialTheme.shapes.small)
                        .align(Alignment.Center),
                    model = artworkUri,
                    onState = { state ->
                        //if it was successful, don't show the placeholder, else show it
                        showArtwork =
                            state !is AsyncImagePainter.State.Error && state !is AsyncImagePainter.State.Empty
                    },
                    contentDescription = "Song Artwork"
                )
            } else {
                PlaceholderCreator(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(MaterialTheme.shapes.small)
                        .align(Alignment.Center),
                    icon = Icons.Default.MusicNote,
                    colorful = false
                )
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 60.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CardListItem(
                modifier = Modifier.weight(0.55f),
                leadingContentIcon = Icons.Default.Info,
                applySemiBoldFontWeight = true,
                headlineContentText = stringResource(
                    id = R.string.mediastore_info
                )
            ) {
                showMediaStoreInfoDialog = true
            }
            Spacer(modifier = Modifier.width(8.dp))
            CardListItem(
                modifier = Modifier.weight(0.45f),
                leadingContentIcon = Icons.Default.Lyrics,
                applySemiBoldFontWeight = true,
                headlineContentText = stringResource(
                    id = R.string.lyrics
                )
            ) {
                TODO()
            }
        }

        CategoryTitle(
            modifier = Modifier.padding(vertical = 6.dp),
            text = stringResource(id = R.string.audio_features)
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
            ) {
                MetadataTag(
                    modifier = Modifier.weight(0.5f),
                    typeOfMetadata = stringResource(id = R.string.bitrate),
                    metadata = metadata.bitrate.toString() + " kbps"
                )
                MetadataTag(
                    modifier = Modifier.weight(0.5f),
                    typeOfMetadata = stringResource(id = R.string.sample_rate),
                    metadata = metadata.sampleRate.toString() + " Hz"
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
            ) {
                MetadataTag(
                    modifier = Modifier.weight(0.5f),
                    typeOfMetadata = stringResource(id = R.string.channels),
                    metadata = metadata.channels.toString()
                )
                MetadataTag(
                    modifier = Modifier.weight(0.5f),
                    typeOfMetadata = stringResource(id = R.string.duration),
                    metadata = metadata.lengthInMilliseconds.toMinutes()
                )
            }
        }


        CategoryTitle(
            modifier = Modifier.padding(vertical = 6.dp),
            text = stringResource(id = R.string.general_tags)
        )
        PreConfiguredOutlinedTextField(
            value = metadataCopy.title.joinOrNullToString(),
            label = stringResource(id = R.string.title),
            modifier = Modifier.fillMaxWidth()
        ) { title ->
            onSaveNewMetadata(
                metadataCopy.copy(
                    title = listOf(title)
                )
            )
        }

        PreConfiguredOutlinedTextField(
            value = metadataCopy.artist.joinOrNullToString(),
            label = stringResource(id = R.string.artist),
            modifier = Modifier.fillMaxWidth()
        ) { artists ->
            onSaveNewMetadata(
                metadataCopy.copy(
                    artist = artists.split(",").map { it.trim() }
                )
            )
        }

        PreConfiguredOutlinedTextField(
            value = metadataCopy.album.joinOrNullToString(),
            label = stringResource(id = R.string.album),
            modifier = Modifier.fillMaxWidth()
        ) { album ->
            onSaveNewMetadata(
                metadataCopy.copy(
                    album = listOf(album)
                )
            )
        }

        PreConfiguredOutlinedTextField(
            value = metadataCopy.albumArtist.joinOrNullToString(),
            label = stringResource(id = R.string.album_artist),
            modifier = Modifier.fillMaxWidth()
        ) { artists ->
            onSaveNewMetadata(
                metadataCopy.copy(
                    albumArtist = artists.split(",").map { it.trim() }
                )
            )
        }

        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
            ) {
                PreConfiguredOutlinedTextField(
                    value = metadataCopy.trackNumber.joinOrNullToString(),
                    label = stringResource(id = R.string.track_number),
                    modifier = Modifier.weight(0.5f)
                ) { trackNumber ->
                    onSaveNewMetadata(
                        metadataCopy.copy(
                            trackNumber = listOf(trackNumber)
                        )
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                PreConfiguredOutlinedTextField(
                    value = metadataCopy.discNumber.joinOrNullToString(),
                    label = stringResource(id = R.string.disc_number),
                    modifier = Modifier.weight(0.5f)
                ) { discNumber ->
                    onSaveNewMetadata(
                        metadataCopy.copy(
                            discNumber = listOf(discNumber)
                        )
                    )
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
            ) {
                PreConfiguredOutlinedTextField(
                    value = metadataCopy.date.joinOrNullToString(),
                    label = stringResource(id = R.string.date),
                    modifier = Modifier.weight(0.5f)
                ) { date ->
                    onSaveNewMetadata(
                        metadataCopy.copy(
                            date = listOf(date)
                        )
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                PreConfiguredOutlinedTextField(
                    value = metadataCopy.genre.joinOrNullToString(),
                    label = stringResource(id = R.string.genre),
                    modifier = Modifier.weight(0.5f)
                ) { genre ->
                    onSaveNewMetadata(
                        metadataCopy.copy(
                            genre = listOf(genre)
                        )
                    )
                }
            }
        }
        CategoryTitle(
            modifier = Modifier.padding(vertical = 6.dp),
            text = stringResource(id = R.string.credits)
        )

        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
            ) {
                PreConfiguredOutlinedTextField(
                    value = metadataCopy.composer.joinOrNullToString(),
                    label = stringResource(id = R.string.composer),
                    modifier = Modifier.weight(0.5f)
                ) { composer ->
                    onSaveNewMetadata(
                        metadataCopy.copy(
                            composer = listOf(composer)
                        )
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                PreConfiguredOutlinedTextField(
                    value = metadataCopy.lyricist.joinOrNullToString(),
                    label = stringResource(id = R.string.lyricist),
                    modifier = Modifier.weight(0.5f)
                ) { lyricist ->
                    onSaveNewMetadata(
                        metadataCopy.copy(
                            lyricist = listOf(lyricist)
                        )
                    )
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
            ) {
                PreConfiguredOutlinedTextField(
                    value = metadataCopy.conductor.joinOrNullToString(),
                    label = stringResource(id = R.string.conductor),
                    modifier = Modifier.weight(0.5f)
                ) { conductor ->
                    onSaveNewMetadata(
                        metadataCopy.copy(
                            conductor = listOf(conductor)
                        )
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                PreConfiguredOutlinedTextField(
                    value = metadataCopy.remixer.joinOrNullToString(),
                    label = stringResource(id = R.string.remixer),
                    modifier = Modifier.weight(0.5f)
                ) { remixer ->
                    onSaveNewMetadata(
                        metadataCopy.copy(
                            remixer = listOf(remixer)
                        )
                    )
                }
            }
            PreConfiguredOutlinedTextField(
                value = metadataCopy.performer.joinOrNullToString(),
                label = stringResource(id = R.string.performer),
                modifier = Modifier.fillMaxWidth()
            ) { performer ->
                onSaveNewMetadata(
                    metadataCopy.copy(
                        performer = listOf(performer)
                    )
                )
            }
        }
    }

    if (showMediaStoreInfoDialog) {
        MediaStoreInfoDialog(
            onDismissRequest = {
                showMediaStoreInfoDialog = false
            }
        )
    }
}