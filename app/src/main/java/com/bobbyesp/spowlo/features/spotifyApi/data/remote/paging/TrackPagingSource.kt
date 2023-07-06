package com.bobbyesp.spowlo.features.spotifyApi.data.remote.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.adamratzman.spotify.SpotifyAppApi
import com.adamratzman.spotify.models.Track
import com.adamratzman.spotify.utils.Market
import com.bobbyesp.spowlo.features.lyrics_downloader.data.local.model.Song
import com.bobbyesp.spowlo.features.lyrics_downloader.data.local.model.toSongs
import com.bobbyesp.spowlo.features.spotifyApi.data.remote.SpotifyApiRequests

class TrackPagingSource(
    private var spotifyApi: SpotifyAppApi? = null,
    private val query: String,
    private val market: Market?,
) : PagingSource<Int, Track>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Track> {
        val offset = params.key ?: 0

        if(spotifyApi == null) {
            val api = SpotifyApiRequests
            spotifyApi = api.provideSpotifyApi()
        }

        return try {
            val response = spotifyApi!!.search.searchTrack(
                query = query,
                limit = 50,
                offset = offset,
                market = market
            )

            if (response.isNotEmpty()) {
                val tracks = response.items

                LoadResult.Page(
                    data = tracks,
                    prevKey = if (offset > 0) offset - params.loadSize else null,
                    nextKey = if (tracks.isNotEmpty()) offset + params.loadSize else null
                )
            } else {
                LoadResult.Error(IllegalStateException("No tracks found"))
            }
        } catch (exception: Exception) {
            LoadResult.Error(exception)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Track>): Int? {
        return state.anchorPosition
    }
}

class TrackAsSongPagingSource(
    private var spotifyApi: SpotifyAppApi? = null,
    private val query: String,
    private val market: Market?,
) : PagingSource<Int, Song>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Song> {
        val offset = params.key ?: 0

        if(spotifyApi == null) {
            val api = SpotifyApiRequests
            spotifyApi = api.provideSpotifyApi()
        }

        return try {
            val response = spotifyApi!!.search.searchTrack(
                query = query,
                limit = 50,
                offset = offset,
                market = market
            )

            if (response.isNotEmpty()) {
                val tracks = response.items

                LoadResult.Page(
                    data = tracks.toSongs(),
                    prevKey = if (offset > 0) offset - params.loadSize else null,
                    nextKey = if (tracks.isNotEmpty()) offset + params.loadSize else null
                )
            } else {
                LoadResult.Error(IllegalStateException("No tracks found"))
            }
        } catch (exception: Exception) {
            LoadResult.Error(exception)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Song>): Int? {
        return state.anchorPosition
    }
}
