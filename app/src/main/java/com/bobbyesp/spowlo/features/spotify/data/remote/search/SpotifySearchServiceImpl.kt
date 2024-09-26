package com.bobbyesp.spowlo.features.spotify.data.remote.search

import com.adamratzman.spotify.endpoints.pub.SearchApi
import com.adamratzman.spotify.models.SearchFilter
import com.adamratzman.spotify.models.SpotifySearchResult
import com.bobbyesp.spowlo.features.spotify.domain.services.SpotifyService
import com.bobbyesp.spowlo.features.spotify.domain.services.search.SpotifySearchService
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class SpotifySearchServiceImpl: SpotifySearchService, KoinComponent {
    private val spotifyService by inject<SpotifyService>()

    override suspend fun search(
        query: String,
        vararg searchTypes: SearchApi.SearchType,
        filters: List<SearchFilter>
    ): SpotifySearchResult {
        val api = spotifyService.getSpotifyApi()
        return api.search.search(query = query, searchTypes = searchTypes, filters = filters)
    }
}