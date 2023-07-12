package com.bobbyesp.spowlo.ui.pages.profile

import SpotifyHorizontalSongCard
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.TweenSpec
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bobbyesp.spowlo.R
import com.bobbyesp.spowlo.ui.components.images.AsyncImageImpl
import com.bobbyesp.spowlo.ui.components.images.PlaceholderCreator
import com.bobbyesp.spowlo.ui.ext.getId

@Composable
fun ProfilePage(
    viewModel: ProfilePageViewModel
) {
    val viewState = viewModel.pageViewState.collectAsStateWithLifecycle()

    LaunchedEffect(true) {
        viewModel.loadPage()
    }

    Crossfade(
        targetState = viewState.value.state,
        label = "Main Crossfade Profile Page"
    ) { state ->
        when (state) {
            is ProfilePageState.Loading -> {
                LoadingPage()
            }

            is ProfilePageState.Error -> {
                ErrorPage()
            }

            is ProfilePageState.Loaded -> {
                PageImplementation(viewModel)
            }
        }
    }


}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PageImplementation(
    viewModel: ProfilePageViewModel
) {
    val context = LocalContext.current
    val viewState = viewModel.pageViewState.collectAsStateWithLifecycle()

    val pageState = viewState.value

    LaunchedEffect(pageState.metadataState) {
        val id = pageState.metadataState?.playableUri?.id?.getId()
        if (id != null) {
            viewModel.searchSongById(id)
        }
    }

    LaunchedEffect(Unit) {
        viewModel.sameSongAsBroadcastVerifier()
    }

    val userInfo = viewState.value.userInformation

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        canScroll = { true },
    )
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
        }, topBar = {
            LargeTopAppBar(
                title = {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = userInfo?.displayName
                                    ?: stringResource(id = R.string.unknown),
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = if (userInfo?.id != null) "@${userInfo.id}" else stringResource(
                                    id = R.string.unknown
                                ),
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(
                                        alpha = 0.6f
                                    )
                                ),
                            )
                        }
                        if (userInfo?.images?.isNotEmpty() == true) {
                            val imageSize =
                                if (scrollBehavior.state.collapsedFraction in 0.7f..1f) 32.dp else 48.dp
                            Box(
                                modifier = Modifier
                                    .animateContentSize(animationSpec = TweenSpec(300))
                                    .size(imageSize)
                                    .clip(CircleShape)
                            ) {
                                AsyncImageImpl(
                                    modifier = Modifier.fillMaxSize(),
                                    model = userInfo.images.lastIndex.let { userInfo.images[it].url },
                                    contentDescription = "Song cover",
                                    contentScale = ContentScale.Crop,
                                )
                            }
                        } else {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                            ) {
                                PlaceholderCreator(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .padding(end = 16.dp)
                                        .clip(CircleShape),
                                    icon = Icons.Default.Person,
                                    colorful = true
                                )
                            }
                        }
                    }
                },
                scrollBehavior = scrollBehavior,
            )
        }) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .nestedScroll(scrollBehavior.nestedScrollConnection)
                .padding(paddingValues),
        ) {
            if (pageState.actualTrack != null) {
                item {
                    Text(
                        text = stringResource(id = R.string.listening_now),
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 18.sp
                        ),
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .padding(top = 8.dp),
                        fontWeight = FontWeight.Bold
                    )
                    SpotifyHorizontalSongCard(
                        modifier = Modifier.padding(16.dp),
                        isPlaying = pageState.playbackState?.playing ?: false,
                        track = pageState.actualTrack,
                    )
                }
            }
        }
    }
}

@Composable
private fun LoadingPage() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun ErrorPage() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Error")
    }
}