package com.bobbyesp.spowlo.features.spotify.domain.repositories

import com.adamratzman.spotify.models.Track

interface SearchRepository {
    suspend fun searchTracks(query: String): Result<List<Track>>
}