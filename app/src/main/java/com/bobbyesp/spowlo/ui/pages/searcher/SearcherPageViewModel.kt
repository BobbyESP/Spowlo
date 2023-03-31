package com.bobbyesp.spowlo.ui.pages.searcher

import android.util.Log
import androidx.lifecycle.ViewModel
import com.adamratzman.spotify.models.SpotifySearchResult
import com.bobbyesp.spowlo.features.spotify_api.data.remote.SpotifyApiRequests
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
class SearcherPageViewModel @Inject constructor() : ViewModel() {

    private val mutableViewStateFlow = MutableStateFlow(ViewState())
    val viewStateFlow = mutableViewStateFlow.asStateFlow()

    data class ViewState(
        val query : String = "",
        val viewState: ViewSearchState = ViewSearchState.Idle,
    )

    private val api = SpotifyApiRequests

    fun updateSearchText(text: String) {
        mutableViewStateFlow.update {
            it.copy(query = text)
        }
    }

    suspend fun makeSearch() {
        mutableViewStateFlow.update {
            it.copy(viewState = ViewSearchState.Loading)
        }
        kotlin.runCatching {
            api.searchAllTypes(viewStateFlow.value.query)
        }.onSuccess { result ->
            if (result == SpotifySearchResult(null, null, null, null, null, null)) {
                mutableViewStateFlow.update { viewState ->
                    viewState.copy(viewState = ViewSearchState.Error("No results found"))
                }
                return@onSuccess
            }
            mutableViewStateFlow.update { viewState ->
                viewState.copy(viewState = ViewSearchState.Success(result))
            }
            Log.d("SearcherPageViewModel", "makeSearch: $result")
        }.onFailure {
            mutableViewStateFlow.update { viewState ->
                viewState.copy(viewState = ViewSearchState.Error(it.message.toString()))
            }
            it.printStackTrace()
        }
    }
}

//create the possible states of the view
sealed class ViewSearchState {
    object Idle : ViewSearchState()
    object Loading : ViewSearchState()
    data class Success(val data: SpotifySearchResult) : ViewSearchState()
    data class Error(val error: String) : ViewSearchState()
}