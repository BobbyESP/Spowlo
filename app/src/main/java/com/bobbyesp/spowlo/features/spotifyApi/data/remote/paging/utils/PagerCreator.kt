package com.bobbyesp.spowlo.features.spotifyApi.data.remote.paging.utils

import android.util.Log
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.cachedIn
import com.adamratzman.spotify.SpotifyClientApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
inline fun <T : Any> createPager(
    isLogged: Boolean = false,
    clientApi: SpotifyClientApi? = null,
    crossinline pagingSourceFactory: (api: SpotifyClientApi) -> PagingSource<Int, T>,
    crossinline nonLoggedSourceFactory: () -> PagingSource<Int, T>,
    coroutineScope: CoroutineScope
): Flow<PagingData<T>>? {
    Log.i("SearchViewModel", "createPager -> isLogged: $isLogged")
    //if we are logged in we can use the api, so we use the pagingSourceFactory. In case that it fails we use the nonLoggedSourceFactory; if we are not logged in we use the nonLoggedSourceFactory
    return try {
        if (isLogged) {
            if (clientApi == null) throw Exception("clientApi is null") // Strange error, should never happen when app is ready for the public.
            Pager(
                config = PagingConfig(
                    pageSize = 20,
                    enablePlaceholders = false,
                    initialLoadSize = 40,
                ),
                pagingSourceFactory = { pagingSourceFactory(clientApi) }
            ).flow.cachedIn(coroutineScope)
        } else {
            Pager(
                config = PagingConfig(
                    pageSize = 20,
                    enablePlaceholders = false,
                    initialLoadSize = 40,
                ),
                pagingSourceFactory = { nonLoggedSourceFactory() }
            ).flow.cachedIn(coroutineScope)
        }
    } catch (e: Exception) {
        Log.e("SearchViewModel", "createPager: ${e.message}. Using nonLoggedSourceFactory")
        Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false,
                initialLoadSize = 40,
            ),
            pagingSourceFactory = { nonLoggedSourceFactory() }
        ).flow.cachedIn(coroutineScope)
    } catch (e: NullPointerException) {
        Log.e("SearchViewModel", "createPager: ${e.message}")
        Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false,
                initialLoadSize = 40,
            ),
            pagingSourceFactory = { nonLoggedSourceFactory() }
        ).flow.cachedIn(coroutineScope)
    }
}