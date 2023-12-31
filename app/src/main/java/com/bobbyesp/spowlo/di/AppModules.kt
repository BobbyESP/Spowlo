package com.bobbyesp.spowlo.di

import android.app.Application
import android.content.Context
import androidx.room.Room.databaseBuilder
import com.bobbyesp.spowlo.db.SpowloMusicDatabase
import com.bobbyesp.spowlo.db.SpowloMusicDatabase.Companion.DB_NAME
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModules {

    @Provides
    @Singleton
    fun provideSpowloMusicDatabase(@ApplicationContext context: Context): SpowloMusicDatabase {
        return databaseBuilder(
            context,
            SpowloMusicDatabase::class.java,
            DB_NAME
        ).build()
    }

    @Provides
    fun provideContext(application: Application): Context {
        return application.applicationContext
    }
}