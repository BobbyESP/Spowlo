package com.bobbyesp.spowlo.features.spotify.di

import com.bobbyesp.spowlo.features.spotify.data.repository.SearchRepositoryImpl
import com.bobbyesp.spowlo.features.spotify.domain.repositories.SearchRepository
import com.bobbyesp.spowlo.features.spotify.domain.services.search.SpotifySearchService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SpotifyRepositoriesModule {
    @Provides
    @Singleton
    fun provideSearchRepository(
        spotifySearchService: SpotifySearchService,
    ): SearchRepository {
        return SearchRepositoryImpl(spotifySearchService)
    }
}