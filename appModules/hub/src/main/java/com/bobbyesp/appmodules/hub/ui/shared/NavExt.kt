package com.bobbyesp.appmodules.hub.ui.shared

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import com.bobbyesp.appmodules.hub.ui.HubNavigationController
import com.bobbyesp.appmodules.hub.ui.LocalHubNavigationController
import com.bobbyesp.appmodules.hub.ui.LocalHubScreenDelegate
import com.bobbyesp.appmodules.hub.ui.ScreenDelegator

fun Modifier.navClickable(
    enabled: Boolean = true,
    enableRipple: Boolean = true,
    onClick: (HubNavigationController) -> Unit
) = composed {
    val navController = LocalHubNavigationController.current

    Modifier.clickable(
        enabled = enabled,
        indication = if (enableRipple) LocalIndication.current else null,
        interactionSource = remember { MutableInteractionSource() },
    ) {
        onClick(navController)
    }
}

fun Modifier.reqNavHubItem(
    enabled: Boolean = true,
    enableRipple: Boolean = true,
    onClick: (HubNavigationController, ScreenDelegator) -> Unit
) = composed {
    val navController = LocalHubNavigationController.current
    val hubScreenDelegate = LocalHubScreenDelegate.current

    Modifier.clickable(
        enabled = enabled,
        indication = if (enableRipple) LocalIndication.current else null,
        interactionSource = remember { MutableInteractionSource() },
    ) {
        onClick(navController, hubScreenDelegate)
    }
}