package com.bobbyesp.appmodules.core.collection

import com.bobbyesp.appmodules.core.SpotifySessionManager
import com.bobbyesp.appmodules.core.api.interalApi.SpotifyInternalApi
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SpotifyCollectionManager @Inject constructor(
    private val spSessionManager: SpotifySessionManager,
    private val spotifyInternalApi: SpotifyInternalApi,
    //private val collectionApi: SpotifyCollectionApi,
    //private val metadataRequester: SpotifyMetadataRequester
){
    fun tracksByArtist(artistId: String) {
        TODO("Not yet implemented")
    }
}