package com.bobbyesp.spowlo.features.spotifyApi.data.remote.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.adamratzman.spotify.SpotifyClientApi
import com.bobbyesp.spowlo.data.local.db.music.MusicAppDatabase
import com.bobbyesp.spowlo.data.local.db.music.entity.TrackEntity
import com.bobbyesp.spowlo.data.local.db.music.entity.toTrackEntityPagingObject

@OptIn(ExperimentalPagingApi::class)
class TrackRemoteMediator(
    private val appMusicDb: MusicAppDatabase,
    private val spotifyApi: SpotifyClientApi,
    private val query: String
) : RemoteMediator<Int, TrackEntity>() {
    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, TrackEntity>
    ): MediatorResult {
        return try {
            val loadKey = when (loadType) {
                LoadType.REFRESH -> 1
                LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
                LoadType.APPEND -> {
                    val lastItem = state.lastItemOrNull()
                    if (lastItem == null) {
                        1
                    } else {
                        (lastItem.id / state.config.pageSize) + 1
                    }
                }
            }

            val tracks = spotifyApi.search.searchTrack(
                query = query,
                offset = loadKey,
                limit = state.config.pageSize
            ).toTrackEntityPagingObject()

            appMusicDb.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    appMusicDb.trackDao().deleteAll()
                }
                val tracksEntities = tracks.items
                appMusicDb.trackDao().upsertAll(tracksEntities)
            }

            MediatorResult.Success(endOfPaginationReached = tracks.items.isEmpty())
        } catch (e: Exception) {
            MediatorResult.Error(e)
        }
    }
}