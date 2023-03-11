package com.bobbyesp.spowlo.ui.pages.searcher

import android.util.Log
import androidx.lifecycle.ViewModel
import com.adamratzman.spotify.models.SpotifySearchResult
import com.bobbyesp.spowlo.features.spotify_api.SpotifyApiRequests
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
class SearcherPageViewModel @Inject constructor() : ViewModel() {

    private val mutableViewStateFlow = MutableStateFlow(ViewState())
    val viewStateFlow = mutableViewStateFlow.asStateFlow()

    data class ViewState(
        val query : String = "",
        val isSearchError : Boolean = false,
        val isSearching : Boolean = false,
        val searchResult : SpotifySearchResult = SpotifySearchResult()
    )

    private val api = SpotifyApiRequests

    fun updateSearchText(text: String) {
        mutableViewStateFlow.update {
            it.copy(query = text)
        }
    }

    suspend fun makeSearch() {
        mutableViewStateFlow.update {
            it.copy(isSearching = true)
        }
        kotlin.runCatching {
            api.searchAllTypes(viewStateFlow.value.query)
        }.onSuccess { result ->
            mutableViewStateFlow.update { viewState ->
                viewState.copy(searchResult = result)
            }
            Log.d("SearcherPageViewModel", "makeSearch: $result")
        }.onFailure {
            mutableViewStateFlow.update { viewState ->
                viewState.copy(isSearchError = true)
            }
            it.printStackTrace()
        }.also {
            mutableViewStateFlow.update {
                it.copy(isSearching = false)
            }
        }
    }
}