package com.bobbyesp.appmodules.core

import xyz.gianlu.librespot.player.Player
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SpotifyPlayerManager @Inject constructor(
    private val spotifySessionManager: SpotifySessionManager,
    private val spotifyConfigManager: SpotifyConfigManager
) {
    @Volatile
    private var _player: Player? = null
    //private var _playerReflect: SpReflect? = null
}