package com.bobbyesp.appmodules.hub.ui

import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier
import com.bobbyesp.appmodules.core.objects.ui_components.UiEvent
import com.bobbyesp.appmodules.core.objects.ui_components.UiItem
import com.bobbyesp.appmodules.hub.ui.shared.reqNavHubItem

object UIEventsHandler {
    fun handle (navController: HubNavigationController, delegate: ScreenDelegator, event: UiEvent) {
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

@Stable
fun Modifier.clickableHubItem(item: UiItem) =
    reqNavHubItem(
        enabled = item.events?.click != null
    ) { navController, delegate ->
        UIEventsHandler.handle(navController, delegate, item.events!!.click!!)
    }