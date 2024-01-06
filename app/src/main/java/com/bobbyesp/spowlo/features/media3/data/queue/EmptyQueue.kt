package com.bobbyesp.spowlo.features.media3.data.queue

import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata

object EmptyQueue : Queue {
    override val preloadItem: MediaMetadata? = null
    override suspend fun getInitialStatus() = Queue.Status(null, emptyList(), -1)
    override fun hasNextPage() = false
    override suspend fun nextPage() = emptyList<MediaItem>()
}