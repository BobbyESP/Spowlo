package com.bobbyesp.spowlo

import com.bobbyesp.library.dto.Song
import java.util.*

abstract class SongDownloaderQueue : Queue<Song> {
    private val queue: MutableList<Song> = mutableListOf()

    override fun offer(song: Song): Boolean {
        return queue.add(song)
    }

    override fun poll(): Song? {
        return if (queue.isNotEmpty()) queue.removeAt(0) else null
    }

    override fun peek(): Song? {
        return queue.firstOrNull()
    }

    fun downloadNextSong() {
        val song = poll()
        if (song != null) {

        }


    }
}