package com.bobbyesp.spowlo.di

import com.bobbyesp.spowlo.data.remote.xManagerAPI
import com.bobbyesp.spowlo.data.remote.xManagerAPI.Companion.BASE_URL
import com.bobbyesp.spowlo.data.respository.APIRepositoryImpl
import com.bobbyesp.spowlo.domain.spotify.repository.APIRepository
import com.bobbyesp.spowlo.domain.spotify.use_case.GetAPIResponse
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideGetApiResponseUseCase(repository: APIRepository): GetAPIResponse {
        return GetAPIResponse(repository)
    }

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
    fun provideAPIHelperImpl(api: xManagerAPI): APIRepository {
        return APIRepositoryImpl(api)
    }

    //Spotify bridge API
    @Provides
    @Singleton
    fun provideGetBridgeApiResponseUseCase(repository: com.bobbyesp.spowlo.domain.spotify_bridge.repository.APIRepository): com.bobbyesp.spowlo.domain.spotify_bridge.use_case.GetAPIResponse {
        return com.bobbyesp.spowlo.domain.spotify_bridge.use_case.GetAPIResponse(repository)
    }

    @Provides
    @Singleton
    fun provideBridgeAPI(): com.bobbyesp.spowlo.data.remote.spotify_bridge.SpotifyBridgeAPI {
        return Retrofit.Builder()
            .baseUrl(com.bobbyesp.spowlo.data.remote.spotify_bridge.SpotifyBridgeAPI.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(com.bobbyesp.spowlo.data.remote.spotify_bridge.SpotifyBridgeAPI::class.java)
    }

    @Provides
    @Singleton
    fun provideBridgeAPIHelperImpl(api: com.bobbyesp.spowlo.data.remote.spotify_bridge.SpotifyBridgeAPI): com.bobbyesp.spowlo.domain.spotify_bridge.repository.APIRepository {
        return com.bobbyesp.spowlo.data.respository.spotify_bridge.APIRepositoryImpl(api)
    }

}