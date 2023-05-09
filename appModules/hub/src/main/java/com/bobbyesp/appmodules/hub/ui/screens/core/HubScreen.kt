package com.bobbyesp.appmodules.hub.ui.screens.core

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bobbyesp.appmodules.core.api.interalApi.SpotifyInternalApi
import com.bobbyesp.appmodules.core.objects.ui_components.UiResponse
import com.bobbyesp.appmodules.core.objects.ui_components.isGrid
import com.bobbyesp.appmodules.core.ui.shared.PagingErrorPage
import com.bobbyesp.appmodules.core.ui.shared.PagingLoadingPage
import com.bobbyesp.appmodules.hub.ui.LocalHubScreenDelegate
import com.bobbyesp.appmodules.hub.ui.UIBinder
import kotlinx.coroutines.launch

@Composable
fun HubScreen(
    needContentPadding: Boolean = true,
    loader: suspend SpotifyInternalApi.() -> UiResponse,
    viewModel: HubScreenViewModel = hiltViewModel(),
    statusBarPadding: Boolean = false,
    onAppBarTitleChange: (String) -> Unit = {},
) {
    val scope = rememberCoroutineScope()
    viewModel.needContentPadding = needContentPadding

    LaunchedEffect(Unit) {
        viewModel.loadPage(onAppBarTitleChange, loader)
    }

    val viewModelState = viewModel.state.collectAsStateWithLifecycle()

    when (viewModelState.value) {
        is HubScreenViewModel.State.Loaded -> {
            CompositionLocalProvider(LocalHubScreenDelegate provides viewModel) {
                LazyVerticalGrid(
                    contentPadding = PaddingValues(if (needContentPadding) 16.dp else 0.dp),
                    verticalArrangement = Arrangement.spacedBy(if (needContentPadding) 8.dp else 0.dp),
                    horizontalArrangement = Arrangement.spacedBy(if (needContentPadding) 8.dp else 0.dp),
                    columns = GridCells.Fixed(2),
                    modifier = if (statusBarPadding) Modifier
                        .fillMaxSize()
                        .statusBarsPadding() else Modifier.fillMaxSize()
                ) {
                    (viewModelState.value as HubScreenViewModel.State.Loaded).data.apply {
                        if (header != null) {
                            item(
                                key = header!!.id,
                                span = {
                                    GridItemSpan(2)
                                },
                                contentType = header!!.component.javaClass.simpleName,
                            ) {
                                UIBinder(header!!)
                            }
                        }

                        body.forEach { item ->
                            if (item.component.isGrid() && !item.children.isNullOrEmpty()) {
                                items(
                                    item.children!!,
                                    key = { childrenItem -> childrenItem.id },
                                    contentType = {
                                        item.component.javaClass.simpleName
                                    }) { childrenUiItem ->
                                    UIBinder(childrenUiItem)
                                }
                            } else {
                                item(span = {
                                    GridItemSpan(if (item.component.isGrid()) 1 else 2)
                                }, key = item.id, contentType = {
                                    item.component.javaClass.simpleName
                                }) {
                                    UIBinder(item, isRenderingInGrid = item.component.isGrid())
                                }
                            }
                        }
                    }
                }
            }
        }

        is HubScreenViewModel.State.Loading -> {
            PagingLoadingPage(modifier = Modifier.fillMaxSize())
        }

        is HubScreenViewModel.State.Error -> {
            PagingErrorPage(
                exception = (viewModelState.value as HubScreenViewModel.State.Error).error,
                onReload = { scope.launch { viewModel.reloadPage(onAppBarTitleChange, loader) } },
                modifier = Modifier.fillMaxSize()
            )
        }
    }

}