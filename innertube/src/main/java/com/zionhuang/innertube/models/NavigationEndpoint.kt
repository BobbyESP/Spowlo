package com.zionhuang.innertube.models

import kotlinx.serialization.Serializable

@Serializable
data class NavigationEndpoint(
    val watchEndpoint: WatchEndpoint? = null,
    val watchPlaylistEndpoint: WatchEndpoint? = null,
    val browseEndpoint: BrowseEndpoint? = null,
    val searchEndpoint: SearchEndpoint? = null,
    val queueAddEndpoint: QueueAddEndpoint? = null,
    val shareEntityEndpoint: ShareEntityEndpoint? = null,
) {
    val endpoint: Endpoint?
        get() = watchEndpoint
            ?: watchPlaylistEndpoint
            ?: browseEndpoint
            ?: searchEndpoint
            ?: queueAddEndpoint
            ?: shareEntityEndpoint
}
