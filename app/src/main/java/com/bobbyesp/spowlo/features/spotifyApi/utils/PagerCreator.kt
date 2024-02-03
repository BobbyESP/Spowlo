package com.bobbyesp.spowlo.features.spotifyApi.utils

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

    val pagerConfig = PagingConfig(
        pageSize = 20,
        enablePlaceholders = false,
        initialLoadSize = 40,
    )

    Log.i("PagerCreator", "createPager -> isLogged: $isLogged")
    //if we are logged in we can use the api, so we use the pagingSourceFactory. In case that it fails we use the nonLoggedSourceFactory; if we are not logged in we use the nonLoggedSourceFactory
    return try {
        if (isLogged) {
            if (clientApi == null || clientApi.token.shouldRefresh()) throw TokenRefreshException("clientApi is null") // Strange error, should never happen when app is ready for the public.
            Log.i("PagerCreator", "createPager: Using pagingSourceFactory")
            Pager(
                config = pagerConfig,
                pagingSourceFactory = { pagingSourceFactory(clientApi) }
            ).flow.cachedIn(coroutineScope)
        } else {
            Log.i("PagerCreator", "createPager: Using nonLoggedSourceFactory")
            Pager(
                config = pagerConfig,
                pagingSourceFactory = { nonLoggedSourceFactory() }
            ).flow.cachedIn(coroutineScope)
        }
    } catch (e: TokenRefreshException) {
        Log.e(
            "PagerCreator",
            "createPager: ${e.message}. Using nonLoggedSourceFactory due to token refresh error."
        )
        Pager(
            config = pagerConfig,
            pagingSourceFactory = { nonLoggedSourceFactory() }
        ).flow.cachedIn(coroutineScope)
    } catch (e: NullPointerException) {
        Log.e("PagerCreator", "createPager: ${e.message}")
        null
    }
}

class TokenRefreshException(message: String) : Exception(message)