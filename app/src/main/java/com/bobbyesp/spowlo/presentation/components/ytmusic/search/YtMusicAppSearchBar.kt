package com.bobbyesp.spowlo.presentation.components.ytmusic.search

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.LibraryMusic
import androidx.compose.material.icons.rounded.Public
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.bobbyesp.spowlo.R
import com.bobbyesp.spowlo.presentation.common.LocalNavController
import com.bobbyesp.spowlo.presentation.common.Route
import com.bobbyesp.ui.components.image.ProfilePicture
import com.zionhuang.innertube.SearchSource
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun YtMusicAppSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: (String) -> Unit,
    active: Boolean,
    onActiveChange: (Boolean) -> Unit,
    searchSource: SearchSource,
    onChangeSearchSource: (SearchSource) -> Unit,
    scrollBehavior: TopAppBarScrollBehavior,
) {
    val navController = LocalNavController.current

    val heightOffsetLimit = with(LocalDensity.current) {
        -(64.dp.toPx() + WindowInsets.systemBars.getTop(this))
    }

    SideEffect {
        if (scrollBehavior.state.heightOffsetLimit != heightOffsetLimit) {
            scrollBehavior.state.heightOffsetLimit = heightOffsetLimit
        }
    }

    SearchBar(
        modifier = Modifier,
        inputField = {
            SearchBarDefaults.InputField(
                modifier = Modifier.offset {
                    IntOffset(
                        x = 0,
                        y = scrollBehavior.state.heightOffset.roundToInt()
                    )
                },
                query = query,
                onQueryChange = onQueryChange,
                onSearch = onSearch,
                expanded = active,
                onExpandedChange = onActiveChange,
                placeholder = {
                    Text(
                        text = stringResource(
                            if (!active) R.string.search
                            else when (searchSource) {
                                SearchSource.LOCAL -> R.string.search_local_library
                                SearchSource.ONLINE -> R.string.search_yt_music
                            }
                        )
                    )
                },
                leadingIcon = {
                    when (active) {
                        true -> IconButton(onClick = { onActiveChange(false) }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                                contentDescription = stringResource(R.string.go_back)
                            )
                        }

                        false -> IconButton(onClick = {
                            onActiveChange(true)
                        }) {
                            Icon(
                                imageVector = Icons.Rounded.Search,
                                contentDescription = stringResource(R.string.search)
                            )
                        }
                    }
                },
                trailingIcon = {
                    Row(
                        modifier = Modifier.padding(end = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        IconButton(onClick = {
                            onChangeSearchSource(
                                when (searchSource) {
                                    SearchSource.LOCAL -> SearchSource.ONLINE
                                    SearchSource.ONLINE -> SearchSource.LOCAL
                                }
                            )
                        }) {
                            Crossfade(
                                targetState = active, label = "Crossfade between search bar trailing icons"
                            ) { active ->
                                when (active) {
                                    true -> when (searchSource) {
                                        SearchSource.LOCAL -> Icon(
                                            imageVector = Icons.Rounded.LibraryMusic,
                                            contentDescription = stringResource(R.string.local_library)
                                        )

                                        SearchSource.ONLINE -> Icon(
                                            imageVector = Icons.Rounded.Public,
                                            contentDescription = stringResource(R.string.settings)
                                        )
                                    }

                                    false -> Icon(
                                        imageVector = Icons.Rounded.Settings,
                                        contentDescription = stringResource(R.string.settings)
                                    )
                                }
                            }
                        }
                        AnimatedVisibility(visible = !active) {
                            ProfilePicture(name = "Bobby", size = 32, onClick = {
                                navController.navigate(Route.OptionsDialog)
                            })
                        }
                    }
                },
            )
        },
        expanded = active,
        onExpandedChange = onActiveChange,
        content = {

        },
    )
}