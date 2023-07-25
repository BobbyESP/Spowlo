package com.bobbyesp.spowlo.features.spotifyApi.data.remote.paging.client

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.adamratzman.spotify.SpotifyClientApi
import com.adamratzman.spotify.models.SearchFilter
import com.adamratzman.spotify.models.Track
import com.adamratzman.spotify.utils.Market

//class SearchingClientPagingSource(
//    private var spotifyApi: SpotifyClientApi,
//    private var query : String,
//    private val market: Market = Market.FROM_TOKEN,
//    private val filters : List<SearchFilter> = emptyList(),
//) : PagingSource<Int, SpotifySearchResult>() {
//    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, SpotifySearchResult> {
//        val offset = params.key ?: 0
//
//        return try {
//            val response = spotifyApi.search.searchAllTypes(
//                query = query,
//                market = market,
//                filters = filters,
//                offset = offset,
//                limit = params.loadSize,
//            )
//
//            LoadResult.Page(
//                data = response,
//                prevKey = if (offset > 0) offset - params.loadSize else null,
//                nextKey = if (response.results.isNotEmpty()) offset + params.loadSize else null
//            )
//        } catch (exception: Exception) {
//            LoadResult.Error(exception)
//        }
//    }
//
//    override fun getRefreshKey(state: PagingState<Int, SpotifySearchResult>): Int? {
//        return state.anchorPosition
//    }
//}

class SearchTracksClientPagingSource(
    private var spotifyApi: SpotifyClientApi,
    private var query: String,
    private val market: Market = Market.FROM_TOKEN,
    private val filters: List<SearchFilter> = emptyList(),
) : PagingSource<Int, Track>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Track> {
        val offset = params.key ?: 0

        return try {
            val response = spotifyApi.search.searchTrack(
                query = query,
                market = market,
                filters = filters,
                offset = offset,
                limit = params.loadSize,
            )

            LoadResult.Page(
                data = response.items,
                prevKey = if (offset > 0) offset - params.loadSize else null,
                nextKey = if (response.items.isNotEmpty()) offset + params.loadSize else null
            )
        } catch (exception: Exception) {
            LoadResult.Error(exception)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Track>): Int? {
        return state.anchorPosition
    }
}