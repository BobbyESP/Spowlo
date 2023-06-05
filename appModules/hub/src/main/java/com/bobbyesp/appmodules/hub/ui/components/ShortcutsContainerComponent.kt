package com.bobbyesp.appmodules.hub.ui.components

import androidx.compose.runtime.Composable
import com.bobbyesp.appmodules.core.objects.ui_components.UiItem
import com.bobbyesp.appmodules.hub.ui.UIBinder

@Composable
fun ShortcutsContainerComponent(
    children: List<UiItem>
) {
    children.forEach {
        UIBinder(it)
    }
}