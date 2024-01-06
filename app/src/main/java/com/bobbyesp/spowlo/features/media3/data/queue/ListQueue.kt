package com.bobbyesp.spowlo.features.media3.data.queue

import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata

class ListQueue(
    val title: String? = null,
    val items: List<MediaItem>,
    val startIndex: Int = 0,
    val position: Long = 0L,
) : Queue {
    override val preloadItem: MediaMetadata? = null
    override suspend fun getInitialStatus() = Queue.Status(title, items, startIndex, position)
    override fun hasNextPage(): Boolean = false
    override suspend fun nextPage() = throw UnsupportedOperationException()
}