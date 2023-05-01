package com.bobbyesp.appmodules.hub.ui.dac

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bobbyesp.appmodules.hub.BuildConfig
import com.bobbyesp.appmodules.hub.ui.components.dac.SongsShortcutsGrid
import com.bobbyesp.appmodules.hub.ui.components.dac.toolbars.ToolbarComponentV1Binder
import com.bobbyesp.appmodules.hub.ui.components.dac.toolbars.ToolbarComponentV2Binder
import com.google.protobuf.Message
import com.spotify.home.dac.component.v1.proto.ShortcutsSectionComponent
import com.spotify.home.dac.component.v1.proto.ToolbarComponent
import com.spotify.home.dac.component.v2.proto.ToolbarComponentV2

@Composable
fun DacComponentRenderer(
    item: Message,
    onNavigateToRequested : (String) -> Unit
) {
    when (item) {

        //////*Home page*//////
        //Top bars
        is ToolbarComponent -> ToolbarComponentV1Binder(item = item, onNavigateToRequested)
        is ToolbarComponentV2 -> ToolbarComponentV2Binder(item = item, onNavigateToRequested)

        //Song shortcuts
        is ShortcutsSectionComponent -> SongsShortcutsGrid(item)



        else -> {
            if (BuildConfig.DEBUG) {
                Text("DAC proto-known, but UI-unknown component: ${item::class.java.simpleName}\n\n${item}", modifier = Modifier.padding(16.dp))
            }
        }
    }
}