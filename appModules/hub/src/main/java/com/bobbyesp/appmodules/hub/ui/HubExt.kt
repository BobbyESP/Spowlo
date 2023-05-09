package com.bobbyesp.appmodules.hub.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.bobbyesp.appmodules.core.objects.ui_components.UiResponse
import com.bobbyesp.appmodules.core.ui.shared.PagingErrorPage
import com.bobbyesp.appmodules.core.ui.shared.PagingLoadingPage
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun HubScaffold(
    appBarTitle: String,
    state: UiState,
    viewModel: ScreenDelegator,
    toolbarOptions: ToolbarOptions = ToolbarOptions(),
    onBackRequested: () -> Unit,
    reloadFunc: suspend () -> Unit
) {
    val scope = rememberCoroutineScope()
    val scrollBehavior =
        if (toolbarOptions.alwaysVisible) TopAppBarDefaults.exitUntilCollapsedScrollBehavior() else TopAppBarDefaults.pinnedScrollBehavior()

    when (state) {
        is UiState.Loaded -> {
            Scaffold(
                topBar = {
                    if (toolbarOptions.big) {
                        LargeTopAppBar(
                            title = {
                                Text(appBarTitle, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            },
                            navigationIcon = {
                                IconButton(onClick = { onBackRequested() }) {
                                    Icon(Icons.Rounded.ArrowBack, null)
                                }
                            },
                            colors = TopAppBarDefaults.largeTopAppBarColors(),
                            scrollBehavior = scrollBehavior
                        )
                    } else {
                        TopAppBar(
                            title = {
                                Text(
                                    appBarTitle,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.alpha(scrollBehavior.state.overlappedFraction)
                                )
                            },
                            navigationIcon = {
                                IconButton(onClick = { onBackRequested() }) {
                                    Icon(Icons.Rounded.ArrowBack, null)
                                }
                            },
                            colors = if (toolbarOptions.alwaysVisible) topAppBarColors(
                            ) else topAppBarColors(
                                containerColor = Color.Transparent,
                                scrolledContainerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(
                                    3.dp
                                )
                            ),
                            scrollBehavior = scrollBehavior
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxSize()
                    .nestedScroll(scrollBehavior.nestedScrollConnection),
                contentWindowInsets = WindowInsets(top = 0.dp)
            ) { padding ->
                CompositionLocalProvider(LocalHubScreenDelegate provides viewModel) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxHeight()
                            .let { if (toolbarOptions.alwaysVisible) it.padding(padding) else it }
                    ) {
                        state.data.apply {
                            if (header != null) {
                                item(
                                    key = header!!.id,
                                    contentType = header!!.component.javaClass.simpleName,
                                ) {
                                    UIBinder(header!!)
                                }
                            }
                            items(
                                body,
                                key = { it.id },
                                contentType = { it.component.javaClass.simpleName }) {
                                Box(modifier = Modifier.animateItemPlacement()) {
                                    UIBinder(it)
                                }
                            }

                        }
                    }
                }
            }
        }

        is UiState.Error -> PagingErrorPage(
            exception = state.error,
            onReload = { scope.launch { reloadFunc() } },
            modifier = Modifier.fillMaxSize()
        )

        UiState.Loading -> PagingLoadingPage(Modifier.fillMaxSize())
    }
}

sealed class UiState {
    object Loading : UiState()
    class Error(val error: Exception) : UiState()
    class Loaded(val data: UiResponse) : UiState()
}

class ToolbarOptions(
    val big: Boolean = false,
    val alwaysVisible: Boolean = false
)