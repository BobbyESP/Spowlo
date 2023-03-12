package com.bobbyesp.spowlo.ui.pages.searcher

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bobbyesp.spowlo.R
import com.bobbyesp.spowlo.ui.components.HorizontalDivider
import com.bobbyesp.spowlo.ui.components.songs.search_feat.SearchingSongComponent
import com.bobbyesp.spowlo.ui.theme.harmonizeWithPrimary
import kotlinx.coroutines.delay

@Composable
fun SearcherPage(
    searcherPageViewModel: SearcherPageViewModel = hiltViewModel()
) {
    val viewState by searcherPageViewModel.viewStateFlow.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        SearcherPageImpl(
            viewState = viewState,
            onValueChange = { query ->
                searcherPageViewModel.updateSearchText(query)
            }
        )
    }
    LaunchedEffect(viewState.query) {
        if (viewState.query.isEmpty()) return@LaunchedEffect
        delay(300)
        searcherPageViewModel.makeSearch()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearcherPageImpl(
    viewState: SearcherPageViewModel.ViewState,
    onValueChange: (String) -> Unit
) {
    Scaffold(modifier = Modifier.fillMaxSize()) {
        with(viewState) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it)
            ) {
                item {
                    QueryTextBox(
                        modifier = Modifier.padding(),
                        query = query,
                        onValueChange = { query ->
                            onValueChange(query)
                        }
                    )
                }
                item {
                    Spacer(modifier = Modifier.padding(vertical = 4.dp))
                }
                if (searchResult.tracks != null) {
                    items(searchResult.tracks!!.size) { track ->
                        with(searchResult.tracks!![track]) {
                            var artists: List<String> = this.artists.map { artist -> artist.name }
                            SearchingSongComponent(
                                artworkUrl = album.images[0].url,
                                songName = this.name,
                                artists = artists.joinToString(", "),
                                spotifyUrl = this.externalUrls.spotify ?: ""
                            )
                        }
                        //if it is not the last item, add a horizontal divider
                        if (track != searchResult.tracks!!.size - 1) {
                            HorizontalDivider(
                                modifier = Modifier.alpha(0.45f),
                                color = MaterialTheme.colorScheme.primary.harmonizeWithPrimary()
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
@Composable
fun QueryTextBox(
    modifier: Modifier = Modifier,
    query: String,
    onValueChange: (String) -> Unit
) {
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    val softwareKeyboardController = LocalSoftwareKeyboardController.current

    OutlinedTextField(
        value = query,
        onValueChange = onValueChange,
        placeholder = {
            if (query.isEmpty()) {
                Text(text = stringResource(id = R.string.searcher_page_query_text_box_label))
            }
        },
        modifier = modifier
            .padding(16.dp)
            .fillMaxWidth()
            .focusRequester(focusRequester),
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Search
        ),
        keyboardActions = KeyboardActions(
            onSearch = {
                focusManager.clearFocus()
                softwareKeyboardController?.hide()
            }
        ),
        leadingIcon = {
            Icon(imageVector = Icons.Rounded.Search, contentDescription = null)
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = { onValueChange("") }) {
                    Icon(imageVector = Icons.Rounded.Close, contentDescription = null)
                }
            }
        },
        singleLine = true,
        colors = TextFieldDefaults.outlinedTextFieldColors(
            containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(
                8.dp
            ), unfocusedBorderColor = MaterialTheme.colorScheme.surfaceVariant
        ),
    )
}