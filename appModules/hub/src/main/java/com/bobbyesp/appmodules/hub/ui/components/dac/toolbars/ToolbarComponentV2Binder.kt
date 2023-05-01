package com.bobbyesp.appmodules.hub.ui.components.dac.toolbars

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.bobbyesp.appmodules.core.ext.dynamicUnpack
import com.bobbyesp.appmodules.hub.R
import com.bobbyesp.appmodules.hub.ui.components.PlaceholderType
import com.bobbyesp.appmodules.hub.ui.components.PreviewableAsyncImage
import com.bobbyesp.uisdk.components.topBar.ToolbarItem
import com.spotify.home.dac.component.v1.proto.ToolbarItemFeedComponent
import com.spotify.home.dac.component.v1.proto.ToolbarItemListeningHistoryComponent
import com.spotify.home.dac.component.v1.proto.ToolbarItemSettingsComponent
import com.spotify.home.dac.component.v2.proto.ToolbarComponentV2

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ToolbarComponentV2Binder(
    item: ToolbarComponentV2,
    onNavigateTo: (String) -> Unit,
) {
    TopAppBar(
        title = {
            Column {
                Text(item.dayPartMessage, fontWeight = FontWeight.SemiBold)
                Text(
                    text = stringResource(id = R.string.spowlo_greetings),
                    fontWeight = FontWeight.Normal,
                    modifier = Modifier.padding(vertical = 4.dp),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    ),
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1
                )
            }
        },
        actions = {
            item.itemsList.forEach {
                when (val protoItem = it.dynamicUnpack()) {
                    is ToolbarItemFeedComponent -> ToolbarItem(
                        Icons.Rounded.Notifications,
                        protoItem.navigateUri,
                        protoItem.title,
                        onNavigateTo
                    )

                    is ToolbarItemListeningHistoryComponent -> ToolbarItem(
                        Icons.Rounded.History,
                        protoItem.navigateUri,
                        protoItem.title,
                        onNavigateTo
                    )

                    is ToolbarItemSettingsComponent -> ToolbarItem(
                        Icons.Rounded.Settings,
                        protoItem.navigateUri,
                        protoItem.title,
                        onNavigateTo
                    )
                }
            }
        },
        modifier = Modifier.statusBarsPadding(),
        windowInsets = WindowInsets(top = 0.dp),
        navigationIcon = {
            IconButton(onClick = {
                onNavigateTo("spotify:config") // TODO until implementation of user pages
            }, modifier = Modifier.padding(start = 8.dp, end = 6.dp)) {
                PreviewableAsyncImage(
                    imageUrl = item.profileButton.imageUri,
                    placeholderType = PlaceholderType.User,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(
                            CircleShape
                        )
                )
            }
        }
    )
}