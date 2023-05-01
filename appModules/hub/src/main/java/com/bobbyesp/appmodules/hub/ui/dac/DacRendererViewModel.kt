package com.bobbyesp.appmodules.hub.ui.dac

import androidx.lifecycle.ViewModel
import com.bobbyesp.appmodules.core.api.interalApi.SpotifyInternalApi
import com.bobbyesp.appmodules.core.ext.dynamicUnpack
import com.bobbyesp.spowlo.proto.ErrorComponent
import com.google.protobuf.Any
import com.google.protobuf.Message
import com.spotify.dac.api.components.VerticalListComponent
import com.spotify.dac.api.v1.proto.DacResponse
import com.spotify.dac.player.v1.proto.PlayCommand
import com.spotify.home.dac.component.v1.proto.HomePageComponent
import com.spotify.home.dac.component.v1.proto.ToolbarComponent
import com.spotify.home.dac.component.v2.proto.ToolbarComponentV2
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class DacRendererViewModel @Inject constructor(
    private val spotifyInternalApi: SpotifyInternalApi,
    //private val spotifyPlayerServiceManager: SpotifyPlayerServiceManager,
    // private val moshi: Moshi
) : ViewModel(), DacDelegator {

    var facet = "default"

    private val _state = MutableStateFlow<State>(State.Loading)
    val state = _state.asStateFlow()

    suspend fun loadPage(loader: suspend SpotifyInternalApi.(String) -> DacResponse) {
        _state.value = try {
            val (sticky, list) = withContext(Dispatchers.Default) {
                val messages = parseMessages(
                    when (val protoList =
                        spotifyInternalApi.loader(facet).component.dynamicUnpack()) {
                        is VerticalListComponent -> protoList.componentsList
                        is HomePageComponent -> protoList.componentsList
                        else -> error("Invalid root for DAC renderer! Found: ${protoList.javaClass.simpleName}")
                    }
                )

                if (messages.first() is ToolbarComponent || messages.first() is ToolbarComponentV2) {
                    messages.first() to messages.drop(1)
                } else {
                    null to messages
                }
            }

            State.Loaded(sticky, list)
        } catch (e: Exception) {
            e.printStackTrace()
            State.Error(e)
        }
    }

    private fun parseMessages(list: List<Any>): List<Message> = list.map { item ->
        try {
            item.dynamicUnpack()
        } catch (e: ClassNotFoundException) {
            ErrorComponent.newBuilder().setType(ErrorComponent.ErrorType.UNSUPPORTED)
                .setMessage(e.message).build()
        } catch (e: java.lang.Exception) {
            ErrorComponent.newBuilder().setType(ErrorComponent.ErrorType.GENERIC_EXCEPTION)
                .setMessage(e.message + "\n\n" + e.stackTraceToString()).build()
        }
    }

    suspend fun reloadPage(loader: suspend SpotifyInternalApi.(String) -> DacResponse) {
        _state.value = State.Loading
        loadPage(loader)
    }

    override fun dispatchPlay(command: PlayCommand) {
        TODO("Not yet implemented")
    }

    sealed class State {
        class Loaded(val sticky: Message?, val data: List<Message>) : State()
        class Error(val error: Exception) : State()
        object Loading : State()
    }

}