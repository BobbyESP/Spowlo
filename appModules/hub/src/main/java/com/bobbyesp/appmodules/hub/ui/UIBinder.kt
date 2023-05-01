package com.bobbyesp.appmodules.hub.ui

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bobbyesp.appmodules.core.objects.ui_components.UiItem
import com.bobbyesp.appmodules.hub.BuildConfig

@Composable
fun UIBinder(
    item: UiItem,
    isRenderingInGrid: Boolean = false,
) {
    when (item.component) {

        else -> {
            if (BuildConfig.DEBUG){
                Text("Unsupported, id = ${item.id}")
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}