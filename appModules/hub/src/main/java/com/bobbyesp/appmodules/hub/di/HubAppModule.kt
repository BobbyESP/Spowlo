package com.bobbyesp.appmodules.hub.di

import com.bobbyesp.appmodules.core.AppEntry
import com.bobbyesp.appmodules.core.di.AppEntryKey
import com.bobbyesp.appmodules.hub.HubAppModule
import com.bobbyesp.appmodules.hub.HubAppModuleImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoMap
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
interface HubAppModule {
    @Singleton
    @Binds
    @IntoMap
    @AppEntryKey(value = HubAppModule::class)
    fun authEntry(entry: HubAppModuleImpl): AppEntry
}