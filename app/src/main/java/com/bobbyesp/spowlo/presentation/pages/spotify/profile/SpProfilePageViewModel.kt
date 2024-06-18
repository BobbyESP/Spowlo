package com.bobbyesp.spowlo.presentation.pages.spotify.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adamratzman.spotify.SpotifyClientApi
import com.adamratzman.spotify.models.SpotifyUserInformation
import com.bobbyesp.spowlo.features.spotify.auth.CredentialsStorer
import com.bobbyesp.utilities.states.NoDataScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SpProfilePageViewModel @Inject constructor(
    private val credentialsStore: CredentialsStorer
) : ViewModel() {
    private lateinit var clientApi: SpotifyClientApi

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    private val mutablePageViewState = MutableStateFlow(PageViewState())
    val pageViewState = mutablePageViewState.asStateFlow()

    init {
        viewModelScope.launch {
            clientApi = credentialsStore.getCredentials().getSpotifyClientPkceApi()
                ?: throw IllegalStateException("Client API is not initialized - Is the user not authenticated?")
        }
    }

    data class PageViewState(
        val state: NoDataScreenState = NoDataScreenState.Error(IllegalStateException("Not loaded - Just a test")),
        val profileInformation: SpotifyUserInformation? = null
    )

    private suspend fun loadProfileInformation() {
        val userData = viewModelScope.async { clientApi.users.getClientProfile() }.await()
        mutablePageViewState.update {
            it.copy(
                state = NoDataScreenState.Success,
                profileInformation = userData
            )
        }
    }

    sealed class UiEvent {

    }
}