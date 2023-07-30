package com.bobbyesp.spowlo.features.spotifyApi.data.remote.paging.client

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.adamratzman.spotify.SpotifyClientApi
import com.adamratzman.spotify.models.Artist
import com.adamratzman.spotify.models.SearchFilter
import com.adamratzman.spotify.models.SimpleAlbum
import com.adamratzman.spotify.models.SimplePlaylist
import com.adamratzman.spotify.models.Track
import com.adamratzman.spotify.utils.Market
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

class SearchSimpleAlbumsClientPagingSource(
    private var spotifyApi: SpotifyClientApi,
    private var query: String,
    private val market: Market = Market.FROM_TOKEN,
    private val filters: List<SearchFilter> = emptyList(),
) : PagingSource<Int, SimpleAlbum>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, SimpleAlbum> {
        val offset = params.key ?: 0

        return try {
            val response = spotifyApi.search.searchAlbum(
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

    override fun getRefreshKey(state: PagingState<Int, SimpleAlbum>): Int? {
        return state.anchorPosition
    }
}

class SearchArtistsClientPagingSource(
    private var spotifyApi: SpotifyClientApi,
    private var query: String,
    private val market: Market = Market.FROM_TOKEN,
    private val filters: List<SearchFilter> = emptyList(),
) : PagingSource<Int, Artist>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Artist> {
        val offset = params.key ?: 0

        return try {
            val response = spotifyApi.search.searchArtist(
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

    override fun getRefreshKey(state: PagingState<Int, Artist>): Int? {
        return state.anchorPosition
    }
}

class SearchSimplePlaylistsClientPagingSource(
    private var spotifyApi: SpotifyClientApi,
    private var query: String,
    private val market: Market = Market.FROM_TOKEN,
    private val filters: List<SearchFilter> = emptyList(),
) : PagingSource<Int, SimplePlaylist>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, SimplePlaylist> {
        val offset = params.key ?: 0

        return try {
            val response = spotifyApi.search.searchPlaylist(
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

    override fun getRefreshKey(state: PagingState<Int, SimplePlaylist>): Int? {
        return state.anchorPosition
    }
}
