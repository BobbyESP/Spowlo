package com.bobbyesp.appModules.auth.ui.auth

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bobbyesp.appmodules.core.SpotifyAuthManager
import com.bobbyesp.appmodules.core.SpotifyConfigManager
import com.bobbyesp.appmodules.core.SpotifySessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class AuthScreenViewModel @Inject constructor(
    private val authManager: SpotifyAuthManager,
    private val spSessionManager: SpotifySessionManager,
    private val spConfigurationManager: SpotifyConfigManager
) : ViewModel() {
    private val _isAuthInProgress = mutableStateOf(false)
    val isAuthInProgress: State<Boolean> = _isAuthInProgress

    fun auth(
        username: String,
        password: String,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit,
    ) {
        if (isAuthInProgress.value) return

        viewModelScope.launch {
            _isAuthInProgress.value = true

            when (val result = authManager.authWith(username, password)) {
                SpotifyAuthManager.AuthResult.Success -> onSuccess()
                is SpotifyAuthManager.AuthResult.Exception -> onFailure("Java Error: ${result.e.message}")
                is SpotifyAuthManager.AuthResult.SpError -> onFailure(
                    result.msg
                )
            }

            _isAuthInProgress.value = false
        }
    }

    /*private suspend fun modifyDatastore(runOnBuilder: AppConfig.Builder.() -> Unit) { //TODO
        spConfigurationManager.dataStore.updateData {
            it.toBuilder().apply(runOnBuilder).build()
        }
    }*/

}

//@HiltViewModel
//internal class AuthScreenViewModel @Inject constructor(hostSteamClient: HostSteamClient) : ViewModel() {