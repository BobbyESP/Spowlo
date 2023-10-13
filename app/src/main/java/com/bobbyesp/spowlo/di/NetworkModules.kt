package com.bobbyesp.spowlo.di

import com.bobbyesp.spowlo.features.mod_downloader.data.remote.ModsDownloaderAPIService
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
    fun provideModsDownloaderAPI(): ModsDownloaderAPIService {
        return ModsDownloaderAPIService.create()
    }

}