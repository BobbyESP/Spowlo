package com.bobbyesp.spowlo.di

import android.app.Application
import android.content.Context
import com.bobbyesp.spowlo.features.spotify.auth.CredentialsStorer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object AppModules {
    @Provides
    fun provideContext(application: Application): Context {
        return application.applicationContext
    }

    @Provides
    fun provideCredentialsStorer(): CredentialsStorer {
        return CredentialsStorer
    }
}