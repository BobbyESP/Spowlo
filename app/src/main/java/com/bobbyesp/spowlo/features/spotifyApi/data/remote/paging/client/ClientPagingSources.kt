package com.bobbyesp.spowlo.features.spotifyApi.data.remote.paging.client

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.adamratzman.spotify.SpotifyClientApi
import com.adamratzman.spotify.models.SavedAlbum
import com.adamratzman.spotify.models.SavedEpisode
import com.adamratzman.spotify.models.SavedShow
import com.adamratzman.spotify.models.SavedTrack
import com.adamratzman.spotify.models.SimplePlaylist
import com.adamratzman.spotify.utils.Market

class ClientPlaylistsPagingSource(
    private var spotifyApi: SpotifyClientApi,
) : PagingSource<Int, SimplePlaylist>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, SimplePlaylist> {
        val offset = params.key ?: 0

        return try {
            val response = spotifyApi.playlists.getClientPlaylists(
                limit = 25,
                offset = offset,
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

    override fun getRefreshKey(state: PagingState<Int, SimplePlaylist>): Int? {
        return state.anchorPosition
    }
}

class ClientSavedTracksPagingSource(
    private var spotifyApi: SpotifyClientApi,
    private val market: Market?,
) : PagingSource<Int, SavedTrack>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, SavedTrack> {
        val offset = params.key ?: 0

        return try {
            val response = spotifyApi.library.getSavedTracks(
                limit = 25,
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

    override fun getRefreshKey(state: PagingState<Int, SavedTrack>): Int? {
        return state.anchorPosition
    }
}

class ClientSavedAlbumsPagingSource(
    private var spotifyApi: SpotifyClientApi,
    private val market: Market?,
) : PagingSource<Int, SavedAlbum>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, SavedAlbum> {
        val offset = params.key ?: 0

        return try {
            val response = spotifyApi.library.getSavedAlbums(
                limit = 25,
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

    override fun getRefreshKey(state: PagingState<Int, SavedAlbum>): Int? {
        return state.anchorPosition
    }
}

class ClientSavedEpisodesPagingSource(
    private var spotifyApi: SpotifyClientApi,
    private val market: Market?,
) : PagingSource<Int, SavedEpisode>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, SavedEpisode> {
        val offset = params.key ?: 0

        return try {
            val response = spotifyApi.library.getSavedEpisodes(
                limit = 25,
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

    override fun getRefreshKey(state: PagingState<Int, SavedEpisode>): Int? {
        return state.anchorPosition
    }
}

class ClientSavedShowsPagingSource(
    private var spotifyApi: SpotifyClientApi,
) : PagingSource<Int, SavedShow>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, SavedShow> {
        val offset = params.key ?: 0

        return try {
            val response = spotifyApi.library.getSavedShows(
                limit = 25,
                offset = offset,
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

    override fun getRefreshKey(state: PagingState<Int, SavedShow>): Int? {
        return state.anchorPosition
    }
}