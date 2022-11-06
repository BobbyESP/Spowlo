package com.bobbyesp.spowlo.di

import com.bobbyesp.spowlo.data.remote.APIHelper
import com.bobbyesp.spowlo.data.remote.ApiHelperImpl
import com.bobbyesp.spowlo.data.remote.xManagerAPI
import com.bobbyesp.spowlo.data.remote.xManagerAPI.Companion.BASE_URL
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideAPI(): xManagerAPI {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(xManagerAPI::class.java)
    }

    @Provides
    @Singleton
    fun provideAPIHelperImpl(api: xManagerAPI): APIHelper {
        return ApiHelperImpl(api)
    }

}