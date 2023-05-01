package com.bobbyesp.appmodules.hub.ui.dac

import androidx.compose.runtime.Stable
import androidx.compose.runtime.staticCompositionLocalOf
import com.spotify.dac.player.v1.proto.PlayCommand

@Stable
interface DacDelegator {
    fun dispatchPlay(command: PlayCommand)
}

val LocalDacDelegator = staticCompositionLocalOf<DacDelegator> { error("DacDelegator should be initialized") }