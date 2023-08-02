package com.bobbyesp.spowlo.di

import com.bobbyesp.spowlo.features.lyrics_downloader.data.remote.SpotifyLyricService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModules {
    @Provides
    @Singleton
    fun provideSyncedLyricsApi(): SpotifyLyricService {
        return SpotifyLyricService.create()
    }

}