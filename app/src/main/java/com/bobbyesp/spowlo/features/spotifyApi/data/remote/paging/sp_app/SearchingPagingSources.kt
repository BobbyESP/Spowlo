package com.bobbyesp.spowlo.features.spotifyApi.data.remote.paging.sp_app

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.adamratzman.spotify.SpotifyAppApi
import com.adamratzman.spotify.models.Artist
import com.adamratzman.spotify.models.SearchFilter
import com.adamratzman.spotify.models.SimpleAlbum
import com.adamratzman.spotify.models.SimplePlaylist
import com.adamratzman.spotify.models.Track
import com.adamratzman.spotify.utils.Market
import com.bobbyesp.spowlo.features.lyrics_downloader.data.local.model.Song
import com.bobbyesp.spowlo.features.lyrics_downloader.data.local.model.toSongs
import com.bobbyesp.spowlo.features.spotifyApi.data.remote.SpotifyApiRequests

class TrackPagingSource(
    private var spotifyApi: SpotifyAppApi? = null,
    private var query: String,
    private val market: Market = Market.FROM_TOKEN,
    private val filters: List<SearchFilter> = emptyList(),
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
                limit = params.loadSize,
                offset = offset,
                market = market,
                filters = filters,
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

class SimpleAlbumPagingSource(
    private var spotifyApi: SpotifyAppApi? = null,
    private var query: String,
    private val market: Market = Market.FROM_TOKEN,
    private val filters: List<SearchFilter> = emptyList(),
): PagingSource<Int, SimpleAlbum>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, SimpleAlbum> {
        val offset = params.key ?: 0

        if(spotifyApi == null) {
            val api = SpotifyApiRequests
            spotifyApi = api.provideSpotifyApi()
        }

        return try {
            val response = spotifyApi!!.search.searchAlbum(
                query = query,
                limit = params.loadSize,
                offset = offset,
                market = market,
                filters = filters,
            )

            if (response.isNotEmpty()) {
                val albums = response.items

                LoadResult.Page(
                    data = albums,
                    prevKey = if (offset > 0) offset - params.loadSize else null,
                    nextKey = if (albums.isNotEmpty()) offset + params.loadSize else null
                )
            } else {
                LoadResult.Error(IllegalStateException("No albums found"))
            }
        } catch (exception: Exception) {
            LoadResult.Error(exception)
        }
    }
    override fun getRefreshKey(state: PagingState<Int, SimpleAlbum>): Int? {
        return state.anchorPosition
    }
}

class ArtistsPagingSource(
    private var spotifyApi: SpotifyAppApi? = null,
    private var query: String,
    private val market: Market = Market.FROM_TOKEN,
    private val filters: List<SearchFilter> = emptyList(),
): PagingSource<Int, Artist>() {
    override fun getRefreshKey(state: PagingState<Int, Artist>): Int? {
        return state.anchorPosition
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Artist> {
        val offset = params.key ?: 0

        if(spotifyApi == null) {
            val api = SpotifyApiRequests
            spotifyApi = api.provideSpotifyApi()
        }

        return try {
            val response = spotifyApi!!.search.searchArtist(
                query = query,
                limit = params.loadSize,
                offset = offset,
                market = market,
                filters = filters,
            )

            if (response.isNotEmpty()) {
                val artists = response.items

                LoadResult.Page(
                    data = artists,
                    prevKey = if (offset > 0) offset - params.loadSize else null,
                    nextKey = if (artists.isNotEmpty()) offset + params.loadSize else null
                )
            } else {
                LoadResult.Error(IllegalStateException("No artists found"))
            }
        } catch (exception: Exception) {
            LoadResult.Error(exception)
        }
    }
}

class SimplePlaylistClientPagingSource(
    private var spotifyApi: SpotifyAppApi? = null,
    private var query: String,
    private val market: Market = Market.FROM_TOKEN,
    private val filters: List<SearchFilter> = emptyList(),
): PagingSource<Int, SimplePlaylist>() {
    override fun getRefreshKey(state: PagingState<Int, SimplePlaylist>): Int? {
        return state.anchorPosition
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, SimplePlaylist> {
        val offset = params.key ?: 0

        if(spotifyApi == null) {
            val api = SpotifyApiRequests
            spotifyApi = api.provideSpotifyApi()
        }

        return try {
            val response = spotifyApi!!.search.searchPlaylist(
                query = query,
                limit = params.loadSize,
                offset = offset,
                market = market,
                filters = filters,
            )

            if (response.isNotEmpty()) {
                val playlists = response.items

                LoadResult.Page(
                    data = playlists,
                    prevKey = if (offset > 0) offset - params.loadSize else null,
                    nextKey = if (playlists.isNotEmpty()) offset + params.loadSize else null
                )
            } else {
                LoadResult.Error(IllegalStateException("No playlists found"))
            }
        } catch (exception: Exception) {
            LoadResult.Error(exception)
        }
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
                limit = params.loadSize,
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