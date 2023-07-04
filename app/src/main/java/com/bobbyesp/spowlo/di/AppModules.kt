package com.bobbyesp.spowlo.di

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.bobbyesp.spowlo.data.local.db.music.MusicAppDatabase
import com.bobbyesp.spowlo.data.local.db.searching.SearchingHistoryDatabase
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
    fun provideMusicDatabase(@ApplicationContext context: Context): MusicAppDatabase {
        return Room.databaseBuilder(
            context,
            MusicAppDatabase::class.java,
            "music_app_database.db"
        ).build()
    }

    @Singleton
    @Provides
    fun provideSearchingDatabase(@ApplicationContext context: Context): SearchingHistoryDatabase {
        return Room.databaseBuilder(
            context,
            SearchingHistoryDatabase::class.java,
            "searching_history_app_database.db"
        ).build()
    }

    @Provides
    fun provideContext(application: Application): Context {
        return application.applicationContext
    }
}