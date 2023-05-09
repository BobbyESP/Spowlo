package com.bobbyesp.appmodules.downloader.di

import com.bobbyesp.appmodules.core.AppEntry
import com.bobbyesp.appmodules.core.di.AppEntryKey
import com.bobbyesp.appmodules.downloader.DownloaderAppModule
import com.bobbyesp.appmodules.downloader.DownloaderAppModuleImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoMap
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface DownloaderAppModule {
    @Singleton
    @Binds
    @IntoMap
    @AppEntryKey(value = DownloaderAppModule::class)
    fun downloaderEntry(entry: DownloaderAppModuleImpl): AppEntry
}