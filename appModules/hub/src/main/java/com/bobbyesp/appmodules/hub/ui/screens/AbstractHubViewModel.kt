package com.bobbyesp.appmodules.hub.ui.screens

import android.graphics.Color.parseColor
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import com.bobbyesp.appmodules.core.api.SpotifyPartnersApi
import com.bobbyesp.appmodules.core.objects.ui_components.UiResponse
import com.bobbyesp.appmodules.hub.ui.ScreenDelegator
import com.bobbyesp.appmodules.hub.ui.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

abstract class AbstractHubViewModel: ViewModel(), ScreenDelegator  {
    private val _state = MutableStateFlow<UiState>(UiState.Loading)
    val state = _state.asStateFlow()

    val mainAddedState = mutableStateOf(false)
    val imageCache = mutableMapOf<String, Color>()

    val hubTitle get() = (_state.value as? UiState.Loaded)?.data?.title ?: ""

    suspend fun load(loader: suspend () -> UiResponse) {
        _state.value = try {
            UiState.Loaded(loader())
        } catch (e: Exception) {
            e.printStackTrace()
            UiState.Error(e)
        }
    }

    suspend fun reload(loader: suspend () -> UiResponse) {
        _state.value = UiState.Loading
        load(loader)
    }

    /*fun play(spPlayerServiceManager: SpPlayerServiceManager, data: PlayFromContextData) {
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

            Color(parseColor(apiResult)).also { imageCache[url] = it }
        } catch (e: Exception) {
            // e.printStackTrace()
            Color.Transparent
        }
    }

}