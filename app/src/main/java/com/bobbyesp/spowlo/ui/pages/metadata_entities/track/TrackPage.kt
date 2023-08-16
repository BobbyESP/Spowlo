package com.bobbyesp.spowlo.ui.pages.metadata_entities.track

import androidx.activity.compose.BackHandler
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImagePainter
import com.bobbyesp.spowlo.R
import com.bobbyesp.spowlo.features.spotifyApi.utils.TrackSaver
import com.bobbyesp.spowlo.ui.bottomSheets.track.TrackBottomSheet
import com.bobbyesp.spowlo.ui.common.LocalNavController
import com.bobbyesp.spowlo.ui.components.buttons.BackButton
import com.bobbyesp.spowlo.ui.components.cards.songs.horizontal.ArtistHorizontalCard
import com.bobbyesp.spowlo.ui.components.cards.songs.horizontal.MetadataEntityItem
import com.bobbyesp.spowlo.ui.components.images.AsyncImageImpl
import com.bobbyesp.spowlo.ui.components.images.PlaceholderCreator
import com.bobbyesp.spowlo.ui.components.others.AudioPropertyBar
import com.bobbyesp.spowlo.ui.components.others.StackedProfilePictures
import com.bobbyesp.spowlo.ui.components.text.ExtraLargeCategoryTitle
import com.bobbyesp.spowlo.ui.ext.toCompleteString
import com.bobbyesp.spowlo.utils.data.Resource
import com.bobbyesp.spowlo.utils.time.TimeUtils
import com.bobbyesp.spowlo.utils.ui.pages.ErrorPage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.ExperimentalSerializationApi

@Composable
fun TrackPage(
    viewModel: TrackPageViewModel, songId: String
) {
    LaunchedEffect(songId) {
        viewModel.loadTrack(songId)
    }

    val viewState = viewModel.pageViewState.collectAsStateWithLifecycle()
    val navController = LocalNavController.current

    BackHandler {
        navController.popBackStack()
    }

    val scope = rememberCoroutineScope()

    Crossfade(
        modifier = Modifier.fillMaxSize(),
        targetState = viewState.value.state,
        animationSpec = tween(175),
        label = "Track Page Crossfade"
    ) {
        when (it) {
            is TrackPageViewModel.Companion.TrackPageState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            is TrackPageViewModel.Companion.TrackPageState.Success -> {
                TrackPageImplementation(viewModel = viewModel, loadedState = it)
            }

            is TrackPageViewModel.Companion.TrackPageState.Error -> {
                ErrorPage(error = it.e) {
                    scope.launch(Dispatchers.IO) {
                        viewModel.loadTrack(songId)
                    }
                }

            }
        }
    }
}

@OptIn(ExperimentalSerializationApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun TrackPageImplementation(
    viewModel: TrackPageViewModel,
    loadedState: TrackPageViewModel.Companion.TrackPageState.Success
) {
    var showSheet by remember { mutableStateOf(false) }

    val viewState = viewModel.pageViewState.collectAsStateWithLifecycle()

    val config = LocalConfiguration.current
    val navController = LocalNavController.current

    val imageSize =
        (config.screenHeightDp / 3.25) //calculate the image size based on the screen size and the aspect ratio as 1:1 (square) based on the height

    val foundTrack = loadedState.track
    val images = viewState.value.artistsImages

    var showImage by remember {
        mutableStateOf(true)
    }

    val trackData by rememberSaveable(stateSaver = TrackSaver) {
        mutableStateOf(foundTrack)
    }

    val dominantColor = viewState.value.dominantColor ?: MaterialTheme.colorScheme.surface
    val artistsFullString = trackData.artists.joinToString(", ") { artist -> artist.name }

    val artistsList = viewState.value.artists

    val artworkUrl = trackData.album.images.firstOrNull()?.url ?: ""

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(
        state = rememberTopAppBarState(),
        canScroll = { true },
    )

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                modifier = Modifier.background(MaterialTheme.colorScheme.primary),
                title = { },
                navigationIcon = {
                    BackButton {
                        navController.popBackStack()
                    }
                },
                actions = {
                    IconButton(onClick = { showSheet = true }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = stringResource(
                                id = R.string.more_options
                            )
                        )
                    }
                },
                scrollBehavior = scrollBehavior,
            )
        },
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
        ) {
            if (trackData.album.images.firstOrNull()?.url != null) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                brush = Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Transparent, dominantColor.copy(alpha = 0.65f)
                                    ), tileMode = TileMode.Repeated
                                )
                            ), contentAlignment = Alignment.Center
                    ) {
                        if (showImage) {
                            AsyncImageImpl(
                                modifier = Modifier
                                    .size(imageSize.dp)
                                    .aspectRatio(
                                        1f, matchHeightConstraintsFirst = true
                                    )
                                    .clip(MaterialTheme.shapes.small),
                                model = artworkUrl,
                                contentDescription = stringResource(id = R.string.track_artwork),
                                onState = { state ->
                                    //if it was successful, don't show the placeholder, else show it
                                    showImage =
                                        state !is AsyncImagePainter.State.Error && state !is AsyncImagePainter.State.Empty
                                },
                                contentScale = ContentScale.FillBounds,
                            )
                        } else {
                            PlaceholderCreator(
                                modifier = Modifier
                                    .size(imageSize.dp)
                                    .clip(MaterialTheme.shapes.small),
                                icon = Icons.Default.MusicNote,
                                colorful = false
                            )
                        }
                    }
                }
            }
            item {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    dominantColor.copy(alpha = 0.65f),
                                    MaterialTheme.colorScheme.surface
                                ),
                                tileMode = TileMode.Mirror
                            )
                        )
                        .padding(top = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp),
                        horizontalAlignment = Alignment.Start
                    ) {
                        SelectionContainer {
                            Text(
                                text = trackData.name,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.headlineMedium,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        SelectionContainer {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if (images.isNotEmpty()) StackedProfilePictures(
                                    profilePhotos = images, stackSpacing = 20
                                )
                                Text(
                                    text = artistsFullString,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.alpha(alpha = 0.8f)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        SelectionContainer {
                            Text(
                                text = stringResource(id = R.string.track) + " • " + trackData.album.releaseDate?.year.toString(),
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.alpha(alpha = 0.8f)
                            )
                        }
                    }
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                    ) {
                        MetadataEntityItem(
                            modifier = Modifier.padding(top = 4.dp, bottom = 10.dp),
                            contentModifier = Modifier.padding(
                                horizontal = 16.dp, vertical = 12.dp
                            ),
                            songName = trackData.name,
                            artists = artistsFullString,
                            isExplicit = trackData.explicit,
                            surfaceColor = Color.Transparent
                        ) {
                            showSheet = true
                        }
                        Column(
                            modifier = Modifier.padding(
                                horizontal = 16.dp,
                            )
                        ) {
                            Text(
                                text = trackData.album.releaseDate.toCompleteString(),
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = TimeUtils.formatDurationWithText(trackData.durationMs.toLong()),
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
            items(artistsList) { artist ->
                ArtistHorizontalCard(
                    modifier = Modifier.fillMaxWidth(),
                    artist = artist
                ) {

                }
            }

            item {
                ExtraLargeCategoryTitle(
                    modifier = Modifier.padding(start = 6.dp),
                    text = stringResource(id = R.string.audio_features)
                )
            }
            item {
                when (val audioData = viewState.value.audioFeaturesData) {
                    is Resource.Error -> {
                        Text(text = audioData.message)
                    }

                    is Resource.Loading -> {
                        CircularProgressIndicator()
                    }

                    is Resource.Success -> {
                        val successData = audioData.data

                        val featuresMap: Map<String, Float> = mapOf(
                            stringResource(id = R.string.acousticness) to successData?.acousticness!!,
                            stringResource(id = R.string.danceability) to successData.danceability,
                            stringResource(id = R.string.energy) to successData.energy,
                            stringResource(id = R.string.instrumentalness) to successData.instrumentalness,
                            stringResource(id = R.string.liveness) to successData.liveness,
                            stringResource(id = R.string.speechiness) to successData.speechiness,
                            stringResource(id = R.string.valence) to successData.valence,
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Column(
                                modifier = Modifier.weight(0.5f),
                                verticalArrangement = Arrangement.spacedBy(
                                    6.dp
                                )
                            ) {
                                //do a forEach but with the first 4 elements
                                featuresMap.toList().subList(0, 4).forEach { (key, value) ->
                                    AudioPropertyBar(
                                        modifier = Modifier,
                                        property = key,
                                        value = value
                                    )
                                }
                            }
                            Column(
                                modifier = Modifier.weight(0.5f),
                                verticalArrangement = Arrangement.spacedBy(
                                    6.dp
                                )
                            ) {
                                //do a forEach but with the last 4 elements
                                featuresMap.toList().subList(4, 7).forEach { (key, value) ->
                                    AudioPropertyBar(
                                        modifier = Modifier,
                                        property = key,
                                        value = value
                                    )
                                }
                            }
                        }
                    }
                }
            }

//            item {
//                ExtraLargeCategoryTitle(
//                    modifier = Modifier.padding(start = 6.dp),
//                    text = stringResource(id = R.string.audio_analysis)
//                )
//            }
//
//            item {
//                when (val audioData = viewState.value.audioAnalysisData) {
//                    is Resource.Error -> {
//                        Text(text = audioData.message)
//                    }
//
//                    is Resource.Loading -> {
//                        CircularProgressIndicator()
//                    }
//
//                    is Resource.Success -> {
//                        val successData = audioData.data
//
//
//                    }
//                }
//            }
        }
        if (showSheet) {
            TrackBottomSheet(track = trackData) {
                showSheet = false
            }
        }
    }
}