package com.bobbyesp.appmodules.hub.ui.dac

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bobbyesp.appmodules.core.api.interalApi.SpotifyInternalApi
import com.bobbyesp.appmodules.core.ui.shared.PagingErrorPage
import com.bobbyesp.appmodules.core.ui.shared.PagingLoadingPage
import com.bobbyesp.appmodules.hub.ui.components.FilterComponentBinder
import com.bobbyesp.uisdk.components.BackButton
import com.spotify.dac.api.v1.proto.DacResponse
import com.spotify.home.dac.component.experimental.v1.proto.FilterComponent
import kotlinx.coroutines.launch
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DacRendererPage(
    title: String,
    fullscreen: Boolean = false,
    loader: suspend SpotifyInternalApi.(String) -> DacResponse,
    onGoBack: () -> Unit,
    onNavigateToRequested: (String) -> Unit,
    viewModel: DacRendererViewModel = hiltViewModel()
) {
    val scrollBehavior =
        if (!fullscreen) TopAppBarDefaults.exitUntilCollapsedScrollBehavior() else TopAppBarDefaults.pinnedScrollBehavior()
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.loadPage(loader)
    }

    val viewState = viewModel.state.collectAsStateWithLifecycle().value

    Crossfade(targetState = viewState, label = "") { state ->
        when (state) {
            is DacRendererViewModel.State.Loaded -> {
                Scaffold(
                    topBar = {
                        if (fullscreen) {
                            (state as? DacRendererViewModel.State.Loaded)?.sticky?.let { msg ->
                                DacComponentRenderer(msg, onNavigateToRequested)
                            }
                        } else {
                            TopAppBar(title = {
                                Text(title)
                            }, navigationIcon = {
                                BackButton {
                                    onGoBack()
                                }
                            }, scrollBehavior = scrollBehavior)
                        }
                    },
                    modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
                    contentWindowInsets = WindowInsets(top = 0.dp)
                ) { padding ->
                    @Suppress("USELESS_IS_CHECK")
                    AnimatedVisibility(visible = state is DacRendererViewModel.State.Loaded) { //Here we kind of re-render the animation, so this is needed
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxHeight()
                                .padding(padding)
                        ) {
                            (state as? DacRendererViewModel.State.Loaded)?.apply {
                                items(data) { item ->
                                    if (item is FilterComponent) {
                                        FilterComponentBinder(item, viewModel.facet) { nf ->
                                            scope.launch {
                                                viewModel.facet = nf
                                                viewModel.reloadPage(loader)
                                            }
                                        }
                                    } else {
                                        CompositionLocalProvider(LocalDacDelegator provides viewModel) {
                                            DacComponentRenderer(item, onNavigateToRequested)
                                        }
                                    }
                                }

                                item {
                                    Spacer(modifier = Modifier.height(8.dp))
                                }
                            }
                        }
                    }

                    AnimatedVisibility(visible = state is DacRendererViewModel.State.Loading) {
                        PagingLoadingPage(Modifier.fillMaxSize())
                    }
                }
            }

            is DacRendererViewModel.State.Error -> {
                PagingErrorPage(
                    exception = state.error,
                    onReload = { scope.launch { viewModel.reloadPage(loader) } },
                    modifier = Modifier.fillMaxSize()
                )
            }

            DacRendererViewModel.State.Loading -> {
                PagingLoadingPage(Modifier.fillMaxSize())
                // This case is handled in the crossfade animation above.
                // The PagingLoadingPage will be shown with an animated CircularProgressIndicator.
            }
        }
    }
}