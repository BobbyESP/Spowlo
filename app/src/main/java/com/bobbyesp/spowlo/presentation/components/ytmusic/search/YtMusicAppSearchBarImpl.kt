package com.bobbyesp.spowlo.presentation.components.ytmusic.search

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import com.bobbyesp.spowlo.MainActivity
import com.bobbyesp.spowlo.presentation.common.LocalNavController
import com.bobbyesp.spowlo.presentation.common.Route
import com.bobbyesp.utilities.preferences.Preferences
import com.bobbyesp.utilities.preferences.PreferencesKeys.YouTube.SEARCH_SOURCE
import com.zionhuang.innertube.SearchSource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun YtMusicAppSearchBarImpl(
    searchBarScrollBehavior: TopAppBarScrollBehavior,
) {
    val focusManager = LocalFocusManager.current
    val navController = LocalNavController.current
    val activity = LocalContext.current as MainActivity

    val (query, onQueryChange) = rememberSaveable(key = "YtMusicSearchQuery") {
        mutableStateOf("")
    }
    var active by rememberSaveable {
        mutableStateOf(false)
    }
    val onActiveChange: (Boolean) -> Unit = { newActive ->
        active = newActive
        if (!newActive) {
            focusManager.clearFocus()
//            if (childRoutesForProvider(currentProviderRoot.value).fastAny { it.formatAsClassToRoute() == currentRoute.value }) {
//                onQueryChange("")
//            }
            onQueryChange("")
        }
    }

    val onSearch: (String) -> Unit = {
        if (it.isNotEmpty()) {
            onActiveChange(false)
            navController.navigate(Route.YoutubeMusic.SearchNavigator.Search(it))
        }
    }

    var openSearchImmediately: Boolean by remember {
        mutableStateOf(activity.intent?.action == MainActivity.ACTION_SEARCH)
    }

    var searchSource by remember {
        mutableStateOf(Preferences.Enumerations.getValue(SEARCH_SOURCE, SearchSource.ONLINE))
    }

    LaunchedEffect(openSearchImmediately) {
        if (openSearchImmediately) {
            onActiveChange(true)
            openSearchImmediately = false
        }
    }

    YtMusicAppSearchBar(
        query = query,
        onQueryChange = onQueryChange,
        onSearch = onSearch,
        active = active,
        onActiveChange = onActiveChange,
        searchSource = searchSource,
        onChangeSearchSource = {
            searchSource = it
            Preferences.Enumerations.encodeValue(SEARCH_SOURCE, it)
        },
        scrollBehavior = searchBarScrollBehavior,
    )

}