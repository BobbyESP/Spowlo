package com.bobbyesp.spowlo.ui.pages.metadata_entities.track

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
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bobbyesp.spowlo.R
import com.bobbyesp.spowlo.features.spotifyApi.utils.TrackSaver
import com.bobbyesp.spowlo.ui.common.LocalNavController
import com.bobbyesp.spowlo.ui.components.bottomsheets.BottomSheet
import com.bobbyesp.spowlo.ui.components.buttons.BackButton
import com.bobbyesp.spowlo.ui.components.images.AsyncImageImpl
import com.bobbyesp.spowlo.ui.components.others.StackedProfilePictures
import com.bobbyesp.spowlo.utils.ui.pages.ErrorPage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.ExperimentalSerializationApi

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSerializationApi::class)
@Composable
fun TrackPage(
    viewModel: TrackPageViewModel,
    songId: String
) {
    LaunchedEffect(songId) {
        viewModel.loadTrack(songId)
    }

    var showSheet by remember { mutableStateOf(false) }

    val viewState = viewModel.pageViewState.collectAsStateWithLifecycle()
    val isPageLoaded = viewState.value.state is TrackPageViewModel.Companion.TrackPageState.Success

    val config = LocalConfiguration.current
    val navController = LocalNavController.current
    val scope = rememberCoroutineScope()

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    val imageSize =
        (config.screenHeightDp / 3.25) //calculate the image size based on the screen size and the aspect ratio as 1:1 (square) based on the height

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {

        },
        topBar = {
            TopAppBar(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.primary),
                title = { },
                navigationIcon = {
                    BackButton {
                        navController.popBackStack()
                    }
                },
                actions = {
                    if (isPageLoaded) {
                        IconButton(onClick = { showSheet = true }) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = stringResource(
                                    id = R.string.more_options
                                )
                            )
                        }
                    }
                },
                scrollBehavior = scrollBehavior,
            )
        },
    ) { paddingValues ->
        Crossfade(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            targetState = viewState.value.state,
            animationSpec = tween(175),
            label = "Track Page Crossfade"
        ) {
            when (it) {
                is TrackPageViewModel.Companion.TrackPageState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                is TrackPageViewModel.Companion.TrackPageState.Success -> {
                    val foundTrack = it.track
                    val images = it.artistsImages

                    val trackData by rememberSaveable(stateSaver = TrackSaver) {
                        mutableStateOf(foundTrack)
                    }

                    Column(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        if (trackData.album.images.firstOrNull()?.url != null) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 6.dp), contentAlignment = Alignment.Center
                            ) {
                                //calculate the image size based on the screen size and the aspect ratio as 1:1 (square) based on the height
                                AsyncImageImpl(
                                    modifier = Modifier
                                        .size(imageSize.dp)
                                        .aspectRatio(
                                            1f, matchHeightConstraintsFirst = true
                                        )
                                        .clip(MaterialTheme.shapes.small),
                                    model = trackData.album.images.firstOrNull()?.url ?: "",
                                    contentDescription = stringResource(id = R.string.track_artwork),
                                    contentScale = ContentScale.FillBounds,
                                )
                            }
                        }
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp),
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
                                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        if(images.isEmpty()) StackedProfilePictures(
                                            profilePhotos = images,
                                            stackSpacing = 20
                                        )
                                        Text(
                                            text = trackData.artists.joinToString(", ") { artist -> artist.name },
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.SemiBold,
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
                        }
                    }

                    if (showSheet) {
                        BottomSheet(onDismiss = { showSheet = false }) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 6.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if (trackData.album.images.firstOrNull()?.url != null) AsyncImageImpl(
                                    modifier = Modifier
                                        .size(50.dp)
                                        .aspectRatio(
                                            1f, matchHeightConstraintsFirst = true
                                        )
                                        .clip(MaterialTheme.shapes.extraSmall),
                                    model = trackData.album.images.firstOrNull()!!.url,
                                    contentDescription = stringResource(
                                        id = R.string.track_artwork
                                    )
                                )
                                Column(
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = trackData.name,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1
                                    )
                                    Text(
                                        text = trackData.artists.joinToString(", ") { artist -> artist.name },
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Normal,
                                        modifier = Modifier.alpha(alpha = 0.6f),
                                        maxLines = 1
                                    )
                                }
                            }
                            HorizontalDivider()
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .size(200.dp),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(text = stringResource(id = R.string.actions))
                            }
                        }
                    }
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
}