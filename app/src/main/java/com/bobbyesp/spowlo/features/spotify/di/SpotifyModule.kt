package com.bobbyesp.spowlo.features.spotify.di

import com.bobbyesp.spowlo.BuildConfig
import com.bobbyesp.spowlo.features.spotify.data.remote.SpotifyServiceImpl
import com.bobbyesp.spowlo.features.spotify.data.remote.search.SpotifySearchServiceImpl
import com.bobbyesp.spowlo.features.spotify.domain.services.SpotifyService
import com.bobbyesp.spowlo.features.spotify.domain.services.search.SpotifySearchService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SpotifyModule {
    @Provides
    @Named("client_id")
    fun provideClientId(): String {
        return BuildConfig.CLIENT_ID
    }

    @Provides
    @Named("client_secret")
    fun provideClientSecret(): String {
        return BuildConfig.CLIENT_SECRET
    }

    @Provides
    @Singleton
    fun provideSpotifyApiService(
        @Named("client_id") clientId: String,
        @Named("client_secret") clientSecret: String
    ): SpotifyService {
        return SpotifyServiceImpl(clientId, clientSecret)
    }

    @Provides
    @Singleton
    fun provideSpotifySearchService(
        spotifyService: SpotifyService,
    ): SpotifySearchService {
        return SpotifySearchServiceImpl(spotifyService)
    }
}