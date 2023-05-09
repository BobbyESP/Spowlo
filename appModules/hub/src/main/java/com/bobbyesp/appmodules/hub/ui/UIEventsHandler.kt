package com.bobbyesp.appmodules.hub.ui

import androidx.navigation.NavHostController
import com.bobbyesp.appmodules.core.objects.ui_components.UiEvent

object UIEventsHandler {
    fun handle (navController: NavHostController, delegate: ScreenDelegator, event: UiEvent) {
        when (event) {
            is UiEvent.NavigateToUri -> {
                if (event.data.uri.startsWith("http")) {
                    TODO("Handle web links")
                } else {
                    navController.navigate(event.data.uri)
                }
            }

            is UiEvent.PlayFromContext -> delegate.play(event.data)

            UiEvent.Unknown -> {}
        }
    }
}

