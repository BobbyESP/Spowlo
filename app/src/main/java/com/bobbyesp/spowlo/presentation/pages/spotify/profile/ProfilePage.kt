package com.bobbyesp.spowlo.presentation.pages.spotify.profile

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bobbyesp.ui.common.pages.ErrorPage
import com.bobbyesp.ui.common.pages.LoadingPage
import com.bobbyesp.utilities.states.NoDataScreenState

@Composable
fun ProfilePage(
    viewModel: SpProfilePageViewModel
) {
    val viewState = viewModel.pageViewState.collectAsStateWithLifecycle()

    Crossfade(
        modifier = Modifier
            .fillMaxSize(),
        targetState = viewState.value.state,
        label = "Main crossfade Profile Page"
    ) { state ->
        when (state) {
            is NoDataScreenState.Error -> ErrorPage(
                modifier = Modifier.fillMaxSize(),
                exception = state.exception,
            ) {
                //onRetry
            }
            NoDataScreenState.Loading -> LoadingPage()
            NoDataScreenState.Success -> ProfilePageImpl()
        }
    }
}

@Composable
private fun ProfilePageImpl() {

}