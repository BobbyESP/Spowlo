package com.bobbyesp.spowlo.features.spotify.data.repository

import com.adamratzman.spotify.endpoints.pub.SearchApi
import com.adamratzman.spotify.models.Track
import com.bobbyesp.spowlo.features.spotify.domain.repositories.SearchRepository
import com.bobbyesp.spowlo.features.spotify.domain.services.search.SpotifySearchService
import javax.inject.Inject

class SearchRepositoryImpl @Inject constructor(
    private val searchService: SpotifySearchService
) : SearchRepository {
    /**
     * Search for tracks on Spotify. The search does support pagination but this implementation
     * does not support it. For now, it will only return up to 50 results.
     */
    override suspend fun searchTracks(query: String): Result<List<Track>> {
        return try {
            val searchResult = searchService.search(
                query,
                searchTypes = arrayOf(SearchApi.SearchType.Track),
                filters = listOf()
            )
            searchResult.let {
                if (it.tracks == null) return Result.failure(IllegalStateException("Search result is null"))
            }
            Result.success(searchResult.tracks!!.items)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

}