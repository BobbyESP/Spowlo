package com.bobbyesp.spowlo.presentation.components.spotify.search

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.bobbyesp.spowlo.R
import com.bobbyesp.spowlo.features.spotify.domain.SpotifyItemType
import com.bobbyesp.spowlo.presentation.common.LocalNavController
import com.bobbyesp.spowlo.presentation.common.Route
import com.bobbyesp.ui.components.image.ProfilePicture
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpAppSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: (String) -> Unit,
    active: Boolean,
    onActiveChange: (Boolean) -> Unit,
    searchSource: SpotifyItemType,
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
                                SpotifyItemType.TRACKS -> R.string.search_tracks

                                SpotifyItemType.ALBUMS -> R.string.search_albums

                                SpotifyItemType.ARTISTS -> R.string.search_artists

                                SpotifyItemType.PLAYLISTS -> R.string.search_playlists
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
                    AnimatedVisibility(visible = !active) {
                        ProfilePicture(
                            modifier = Modifier.padding(end = 4.dp),
                            name = "Bobby",
                            size = 32,
                            onClick = {
                                navController.navigate(Route.OptionsDialog)
                            }
                        )
                    }
                },
            )
        },
        expanded = active,
        onExpandedChange = onActiveChange,
        content = {

        }
    )

}