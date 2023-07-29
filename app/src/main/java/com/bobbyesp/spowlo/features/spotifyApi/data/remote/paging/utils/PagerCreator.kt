package com.bobbyesp.spowlo.features.spotifyApi.data.remote.paging.utils

import android.content.Context
import android.util.Log
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.cachedIn
import com.adamratzman.spotify.SpotifyClientApi
import com.bobbyesp.spowlo.features.spotifyApi.utils.login.checkSpotifyApiIsValid
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow

suspend inline fun <T : Any> createPager(
    context: Context,
    crossinline pagingSourceFactory: (api: SpotifyClientApi) -> PagingSource<Int, T>,
    crossinline authFailedPagingSource: () -> PagingSource<Int, T>,
    coroutineScope : CoroutineScope
): Flow<PagingData<T>>? {
    return try {
        checkSpotifyApiIsValid(applicationContext = context) { api ->
            Pager(
                config = PagingConfig(
                    pageSize = 20,
                    enablePlaceholders = false,
                    initialLoadSize = 40,
                ),
                pagingSourceFactory = { pagingSourceFactory(api) }
            ).flow.cachedIn(coroutineScope)
        }
    } catch (e: Exception) {
        Log.e("SearchViewModel", "createPager: ${e.message}")
        Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false,
                initialLoadSize = 40,
            ),
            pagingSourceFactory = { authFailedPagingSource() }
        ).flow.cachedIn(coroutineScope)
    }
}
