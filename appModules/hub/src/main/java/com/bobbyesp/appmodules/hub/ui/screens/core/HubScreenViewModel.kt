package com.bobbyesp.appmodules.hub.ui.screens.core

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import com.bobbyesp.appmodules.core.api.interalApi.SpotifyInternalApi
import com.bobbyesp.appmodules.core.collection.SpotifyCollectionManager
import com.bobbyesp.appmodules.core.objects.player.PlayFromContextData
import com.bobbyesp.appmodules.core.objects.ui_components.UiResponse
import com.bobbyesp.appmodules.hub.ui.ScreenDelegator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class HubScreenViewModel @Inject constructor(
    private val spInternalApi: SpotifyInternalApi,
    //private val spPlayerServiceManager: SpPlayerServiceManager, //TODO
    private val spCollectionManager: SpotifyCollectionManager
): ViewModel(), ScreenDelegator {

    private val _state = MutableStateFlow<State>(State.Loading)
    val state = _state.asStateFlow()

    private val nullState = mutableStateOf(false)
    override fun getMainObjectAddedState() = nullState

    // no state handle needed
    var needContentPadding: Boolean = false

    suspend fun loadPage(chg: (String) -> Unit, loader: suspend SpotifyInternalApi.() -> UiResponse) {
        _state.value = try {
            State.Loaded(spInternalApi.loader().also { chg(it.title ?: "") })
        } catch (e: Exception) {
            e.printStackTrace()
            State.Error(e)
        }
    }

    suspend fun reloadPage(chg: (String) -> Unit, loader: suspend SpotifyInternalApi.() -> UiResponse) {
        _state.value = State.Loading
        loadPage(chg, loader)
    }

    override fun isSurroundedWithPadding() = needContentPadding

    override suspend fun calculateDominantColor(url: String, dark: Boolean) = Color.Transparent

    override suspend fun getLikedSongsCount(artistId: String): Int {
        //Todo: return spCollectionManager.tracksByArtist(artistId).size
        return 0
    }

    override fun play(data: PlayFromContextData) {
        TODO("Not yet implemented")
    }

    sealed class State {
        class Loaded(val data: UiResponse) : State()
        class Error(val error: Exception) : State()
        object Loading : State()
    }

}