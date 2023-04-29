package com.bobbyesp.appmodules.hub.ui

import com.bobbyesp.appmodules.core.objects.ui_components.UiEvent
import com.bobbyesp.spowlo.ui.navigation.NavigationController

object UIEventsHandler {
    fun handle (navController: NavigationController, delegate: ScreenDelegator, event: UiEvent) {
        when (event) {
            is UiEvent.NavigateToUri -> {
                if (event.data.uri.startsWith("http")) {
                    navController.openInBrowser(event.data.uri)
                } else {
                    navController.navigate(event.data.uri)
                }
            }

            is UiEvent.PlayFromContext -> delegate.play(event.data)

            UiEvent.Unknown -> {}
        }
    }
}