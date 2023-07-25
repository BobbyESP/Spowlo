package com.bobbyesp.spowlo.ui.pages.search

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bobbyesp.spowlo.ui.common.LocalPlayerAwareWindowInsets
import com.bobbyesp.spowlo.ui.components.dividers.HorizontalDivider
import com.bobbyesp.spowlo.ui.components.searchBar.QueryTextBox

@Composable
fun SearchPage(
    viewModel: SearchViewModel,
) {
    val bottomInsetsAsPadding =
        LocalPlayerAwareWindowInsets.current.asPaddingValues().calculateBottomPadding()
    val viewState = viewModel.pageViewState.collectAsStateWithLifecycle().value

    val (query, onValueChange) = remember {
        mutableStateOf("")
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = bottomInsetsAsPadding)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) {
            QueryTextBox(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                query = query,
                onValueChange = onValueChange,
                onSearchCallback = {

                }
            )
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
            ) {

            }

            HorizontalDivider(modifier = Modifier.padding(16.dp))
        }
    }
}