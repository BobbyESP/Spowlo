package com.bobbyesp.appmodules.hub.ui.components.dac.toolbars

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.bobbyesp.appmodules.core.ext.dynamicUnpack
import com.spotify.home.dac.component.v1.proto.ToolbarComponent
import com.spotify.home.dac.component.v1.proto.ToolbarItemFeedComponent
import com.spotify.home.dac.component.v1.proto.ToolbarItemListeningHistoryComponent
import com.spotify.home.dac.component.v1.proto.ToolbarItemSettingsComponent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ToolbarComponentV1Binder(
    item: ToolbarComponent,
    onNavigateTo: (String) -> Unit,
) {
    TopAppBar(title = {
        Text(item.dayPartMessage)
    }, actions = {
        item.itemsList.forEach {
            when (val protoItem = it.dynamicUnpack()) {
                is ToolbarItemFeedComponent -> ToolbarItem(Icons.Rounded.Notifications, protoItem.navigateUri, protoItem.title, onNavigateTo)
                is ToolbarItemListeningHistoryComponent -> ToolbarItem(Icons.Rounded.History, protoItem.navigateUri, protoItem.title, onNavigateTo)
                is ToolbarItemSettingsComponent -> ToolbarItem(Icons.Rounded.Settings, protoItem.navigateUri, protoItem.title, onNavigateTo)
            }
        }
    }, modifier = Modifier.statusBarsPadding(), windowInsets = WindowInsets(top = 0.dp))
}

@Composable
private fun ToolbarItem(
    icon: ImageVector,
    uriToNavigate: String,
    contentDesc: String,
    onNavigateTo: (String) -> Unit,
) {

    IconButton(onClick = {
        onNavigateTo(uriToNavigate)
    }) {
        Icon(icon, contentDescription = contentDesc)
    }
}