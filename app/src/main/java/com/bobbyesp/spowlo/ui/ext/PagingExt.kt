package com.bobbyesp.spowlo.ui.ext

import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import com.bobbyesp.spowlo.BuildConfig

fun <T : Any> LazyListScope.loadStateContent(
    items: LazyPagingItems<T>?,
    itemCount: Int = 7,
    loadingContent: @Composable () -> Unit
) {
    items?.apply {
        when {
            loadState.refresh is LoadState.Loading -> {
                items(itemCount) {
                    // Render a loading indicator while refreshing
                    loadingContent()
                }
            }

            loadState.append is LoadState.Loading -> {
                items(itemCount) {
                    // Render a loading indicator at the end while loading more items
                    loadingContent()
                }
            }

            loadState.refresh is LoadState.Error -> {
                val errorMessage =
                    (loadState.refresh as LoadState.Error).error.message
                item {
                    // Render an error message if refreshing encounters an error
                    if (errorMessage != null) {
                        if (BuildConfig.DEBUG) Text(errorMessage)
                    }
                }
            }

            loadState.append is LoadState.Error -> {
                val errorMessage =
                    (loadState.append as LoadState.Error).error.message
                item {
                    // Render an error message if loading more items encounters an error
                    if (errorMessage != null) {
                        if (BuildConfig.DEBUG) Text(errorMessage)
                    }
                }
            }
        }
    }
}