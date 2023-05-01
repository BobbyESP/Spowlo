package com.bobbyesp.appmodules.hub

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import com.bobbyesp.appmodules.core.api.SpotifyPartnersApi
import com.bobbyesp.appmodules.core.objects.ui_components.UiResponse
import com.bobbyesp.appmodules.core.utils.Log
import com.bobbyesp.appmodules.hub.ui.ScreenDelegator
import com.bobbyesp.appmodules.hub.ui.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

abstract class BaseHubViewModel: ViewModel(), ScreenDelegator {
    private val _state = MutableStateFlow<UiState>(UiState.Loading)
    val state = _state.asStateFlow()

    val mainAddedState = mutableStateOf(false)
    val imageCache = mutableMapOf<String, Color>()

    val pageUiTitle = (_state.value as? UiState.Loaded)?.data?.title ?: ""

    suspend fun loadPage(loader: suspend () -> UiResponse) {
        _state.update {
            try {
                UiState.Loaded(loader())
            } catch (e: Exception) {
                e.printStackTrace()
                UiState.Error(e)
            }
        }
    }

    suspend fun reloadPage(loader: suspend () -> UiResponse) {
        _state.update {
            UiState.Loading
        }
        loadPage(loader)
    }

   /* fun playSong(spotifyPlayerServiceManager: SpotifyPlayerServiceManager, data: PlayFromContextData) { //TODO
        spPlayerServiceManager.play(data.uri, data.player)
    }*/

    override fun isSurroundedWithPadding() = false
    override fun getMainObjectAddedState() = mainAddedState

    suspend fun calculateDominantColor(partnersApi: SpotifyPartnersApi, url: String, dark: Boolean): Color {
        return try {
            if (imageCache.containsKey(url)) {
                return imageCache[url]!!
            }

            val apiResult = partnersApi.fetchExtractedColors(variables = "{\"uris\":[\"$url\"]}").data.extractedColors[0].let {
                if (dark) it.colorRaw else it.colorDark
            }.hex

            Color(android.graphics.Color.parseColor(apiResult)).also { imageCache[url] = it }
        } catch (e: Exception) {
            Log.w("BaseHubViewModel", "calculateDominantColor: The app was unable to calculate the dominant color of the image. Returning transparent color. Error stack trace: ${e.message}")
            Color.Transparent
        }
    }
}