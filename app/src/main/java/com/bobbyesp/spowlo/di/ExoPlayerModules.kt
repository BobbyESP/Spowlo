@file:OptIn(UnstableApi::class)

package com.bobbyesp.spowlo.di

import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

private const val NO_PLAYER_CACHE = -1

@Module
@InstallIn(SingletonComponent::class)
object ExoPlayerModules {
//
//    @Singleton
//    @Provides
//    fun provideDatabaseProvider(@ApplicationContext context: Context): DatabaseProvider =
//        StandaloneDatabaseProvider(context)
//
//    @Singleton
//    @Provides
//    @PlayerCache
//    fun providePlayerCache(
//        @ApplicationContext context: Context,
//        databaseProvider: DatabaseProvider
//    ): SimpleCache {
//        val constructor = {
//            SimpleCache(
//                context.filesDir.resolve("exoplayer"),
//                when (val cacheSize = MAX_SONG_CACHE_SIZE.getInt(default = DefaultSongCacheSize)) {
//                    NO_PLAYER_CACHE -> NoOpCacheEvictor()
//                    else -> LeastRecentlyUsedCacheEvictor(cacheSize * 1024 * 1024L)
//                },
//                databaseProvider
//            )
//        }
//        constructor().release()
//        return constructor()
//    }
//
//    @Singleton
//    @Provides
//    @DownloadCache
//    fun provideDownloadCache(
//        @ApplicationContext context: Context,
//        databaseProvider: DatabaseProvider
//    ): SimpleCache {
//        val constructor = {
//            SimpleCache(context.filesDir.resolve("download"), NoOpCacheEvictor(), databaseProvider)
//        }
//        constructor().release()
//        return constructor()
//    }
}