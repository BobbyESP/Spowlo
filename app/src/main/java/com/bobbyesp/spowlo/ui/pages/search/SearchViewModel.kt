package com.bobbyesp.spowlo.ui.pages.search

import androidx.lifecycle.ViewModel
import com.adamratzman.spotify.models.SearchFilter
import com.adamratzman.spotify.models.SpotifySearchResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor() : ViewModel() {
    private val mutablePageViewState = MutableStateFlow(PageViewState())
    val pageViewState = mutablePageViewState.asStateFlow()

    data class PageViewState(
        val searchViewState: SearchViewState = SearchViewState.Idle,
        val activeFilter: SearchFilter? = null,
    )

    fun chooseFilter(filter: SearchFilter) {
        val actualFilter = pageViewState.value.activeFilter
        if (actualFilter == filter) {
            mutablePageViewState.value = pageViewState.value.copy(activeFilter = null)
        } else {
            mutablePageViewState.value = pageViewState.value.copy(activeFilter = filter)
        }
    }

    fun chooseFilterAndSearch(filter: SearchFilter) {
        chooseFilter(filter)
        TODO("Implement search")
    }
}

sealed class SearchViewState {
    object Idle : SearchViewState()
    object Loading : SearchViewState()
    data class Success(val data: SpotifySearchResult) : SearchViewState()
    data class Error(val error: Exception) : SearchViewState()
}