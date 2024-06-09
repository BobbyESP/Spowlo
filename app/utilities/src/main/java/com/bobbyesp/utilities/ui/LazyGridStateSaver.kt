package com.bobbyesp.utilities.ui

import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.saveable.rememberSaveable

/**
 * Static field, contains all scroll values
 */
val SaveMap = mutableMapOf<String, KeyParams>()

data class KeyParams(
    val params: String = "",
    val index: Int,
    val scrollOffset: Int
)

/**
 * Save scroll state on all time.
 * @param key value for comparing screen
 * @param params arguments for find different between equals screen
 * @param initialFirstVisibleItemIndex see [LazyGridState.firstVisibleItemIndex]
 * @param initialFirstVisibleItemScrollOffset see [LazyGridState.firstVisibleItemScrollOffset]
 */
@Composable
fun rememberForeverLazyGridState(
    key: String,
    params: String = "",
    initialFirstVisibleItemIndex: Int = 0,
    initialFirstVisibleItemScrollOffset: Int = 0
): LazyGridState {
    val scrollState = rememberSaveable(saver = LazyGridState.Saver) {
        var savedValue = SaveMap[key]
        if (savedValue?.params != params) savedValue = null
        val savedIndex = savedValue?.index ?: initialFirstVisibleItemIndex
        val savedOffset = savedValue?.scrollOffset ?: initialFirstVisibleItemScrollOffset
        LazyGridState(
            savedIndex,
            savedOffset
        )
    }
    DisposableEffect(Unit) {
        onDispose {
            val lastIndex = scrollState.firstVisibleItemIndex
            val lastOffset = scrollState.firstVisibleItemScrollOffset
            SaveMap[key] = KeyParams(params, lastIndex, lastOffset)
        }
    }
    return scrollState
}