package com.bobbyesp.spowlo.presentation.ui.pages.welcome_page

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.adamratzman.spotify.auth.pkce.startSpotifyClientPkceLoginActivity
import com.bobbyesp.spowlo.domain.spotify.web_api.auth.SpotifyPkceLoginActivityImpl
import com.bobbyesp.spowlo.presentation.ui.common.Route
import com.bobbyesp.spowlo.util.PreferencesUtil
import com.bobbyesp.spowlo.util.PreferencesUtil.IS_LOGGED
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

class WelcomePageViewModel@Inject constructor() : ViewModel() {
    private val mutableStateFlow = MutableStateFlow(WelcomePageViewState())
    val stateFlow = mutableStateFlow.asStateFlow()
    private var currentJob: Job? = null

    data class WelcomePageViewState(
        val isLogged: Boolean = false,
        val isLoaded: Boolean = false
    )

    fun loginToSpotify(activity: Activity? = null, navController: NavController){
        activity?.startSpotifyClientPkceLoginActivity(SpotifyPkceLoginActivityImpl::class.java)
        PreferencesUtil.updateValue(IS_LOGGED, true)
        navController.navigate(Route.HOME)

    }


}