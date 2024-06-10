package com.bobbyesp.spowlo.features.spotify.domain.services.search

import com.adamratzman.spotify.endpoints.pub.SearchApi
import com.adamratzman.spotify.models.SearchFilter
import com.adamratzman.spotify.models.SpotifySearchResult

interface SpotifySearchService {
    suspend fun search(
        query: String,
        vararg searchTypes: SearchApi.SearchType,
        filters: List<SearchFilter>
    ): SpotifySearchResult
}