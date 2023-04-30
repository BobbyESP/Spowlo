package com.bobbyesp.appModules.auth.di

import com.bobbyesp.appModules.auth.AuthAppModule
import com.bobbyesp.appModules.auth.AuthAppModuleImpl
import com.bobbyesp.appmodules.core.AppEntry
import com.bobbyesp.appmodules.core.di.AppEntryKey
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoMap
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface AuthAppModule {
    @Singleton
    @Binds
    @IntoMap
    @AppEntryKey(value = AuthAppModule::class)
    fun authEntry(entry: AuthAppModuleImpl): AppEntry
}