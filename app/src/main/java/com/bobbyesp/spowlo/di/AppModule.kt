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
        println("seems to call this")
        return APIRepositoryImpl(api)
    }

}