package com.bobbyesp.appmodules.core.di

import com.bobbyesp.appmodules.core.SpotifySessionManager
import com.bobbyesp.appmodules.core.api.ClientTokenHandler
import com.bobbyesp.appmodules.core.api.SpotifyBlendApi
import com.bobbyesp.appmodules.core.api.SpotifyCollectionApi
import com.bobbyesp.appmodules.core.api.SpotifyColorsLyricsApi
import com.bobbyesp.appmodules.core.api.SpotifyExternalIntegrationApi
import com.bobbyesp.appmodules.core.api.SpotifyPartnersApi
import com.bobbyesp.appmodules.core.api.interalApi.SpotifyInternalApi
import com.bobbyesp.appmodules.core.di.ext.DependencyInjectionExt.interceptRequest
import com.bobbyesp.appmodules.core.utils.SpotifyUtils
import com.bobbyesp.appmodules.core.utils.create
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.protobuf.ProtoConverterFactory
import java.util.*
import javax.inject.Singleton
@Module
@InstallIn(SingletonComponent::class)
object ApiModule {
    @Provides
    @Singleton
    fun provideMoshi(): Moshi = Moshi.Builder().build()

    @Provides
    @Singleton
    fun provideOkHttpClient(tokenHandler: ClientTokenHandler, sessionManager: SpotifySessionManager): OkHttpClient = OkHttpClient.Builder().apply {
        interceptRequest { orig ->
            // 1. Authorization (& client token)
            header("Authorization", "Bearer ${sessionManager.session.tokens().get("playlist-read")}")
            header("client-token", tokenHandler.requestToken())

            // 2. Default headers
            header("User-Agent", "Spotify/${SpotifyUtils.SPOTIFY_APP_VERSION} Android/32 (Pixel 6a)")
            header("Spotify-App-Version", SpotifyUtils.SPOTIFY_APP_VERSION)
            header("App-Platform", "Android")

            // 3. Default GET params
            if (orig.method == "GET" && !orig.url.host.contains("api-partner")) {
                url(orig.url.newBuilder().apply {
                    addQueryParameter("client-timezone", TimeZone.getDefault().id)
                    if (!orig.url.pathSegments.contains("content-filter")) {
                        addQueryParameter("platform", "android")
                        addQueryParameter("locale", sessionManager.session.preferredLocale())
                        addQueryParameter("video", "true")
                        addQueryParameter("podcast", "true")
                        addQueryParameter("application", "nft")
                    }
                }.build())
            }
        }
    }.build()

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit = Retrofit.Builder().apply {
        addConverterFactory(ProtoConverterFactory.create())
        addConverterFactory(MoshiConverterFactory.create())
        baseUrl("https://spclient.wg.spotify.com")
        client(okHttpClient)
    }.build()

    @Provides
    @Singleton
    fun provideInternalApi(retrofit: Retrofit): SpotifyInternalApi = retrofit.create("https://spclient.wg.spotify.com")

    @Provides
    @Singleton
    fun providePartnersApi(retrofit: Retrofit): SpotifyPartnersApi = retrofit.create("https://api-partner.spotify.com")

    @Provides
    @Singleton
    fun provideCollectionApi(retrofit: Retrofit): SpotifyCollectionApi = retrofit.create("https://spclient.wg.spotify.com/collection/v2/")

    @Provides
    @Singleton
    fun provideBlendApi(retrofit: Retrofit): SpotifyBlendApi = retrofit.create("https://spclient.wg.spotify.com")

    @Provides
    @Singleton
    fun provideColorLyricsApi(retrofit: Retrofit): SpotifyColorsLyricsApi = retrofit.create("https://spclient.wg.spotify.com")

    @Provides
    @Singleton
    fun provideExternalIntegrationApi(retrofit: Retrofit): SpotifyExternalIntegrationApi = retrofit.create("https://spclient.wg.spotify.com/external-integration-recs/")
}