package com.bobbyesp.appmodules.core

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SpotifyPlayerManager @Inject constructor(
    private val spotifySessionManager: SpotifySessionManager,
    private val spotifyConfigManager: SpotifyConfigManager
) {

}