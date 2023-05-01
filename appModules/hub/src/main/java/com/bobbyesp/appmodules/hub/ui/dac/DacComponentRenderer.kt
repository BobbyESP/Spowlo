package com.bobbyesp.appmodules.hub.ui.dac

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bobbyesp.appmodules.hub.BuildConfig
import com.bobbyesp.appmodules.hub.ui.components.dac.SongsShortcutsGrid
import com.google.protobuf.Message
import com.spotify.home.dac.component.v1.proto.ShortcutsSectionComponent

@Composable
fun DacComponentRenderer(
    item: Message
) {
    when (item) {
        is ShortcutsSectionComponent -> SongsShortcutsGrid(item)

        else -> {
            if (BuildConfig.DEBUG) {
                Text("DAC proto-known, but UI-unknown component: ${item::class.java.simpleName}\n\n${item}", modifier = Modifier.padding(16.dp))
            }
        }
    }
}