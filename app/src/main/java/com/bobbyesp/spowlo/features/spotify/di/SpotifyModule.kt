package com.bobbyesp.spowlo.features.spotify.di

import com.bobbyesp.spowlo.BuildConfig
import com.bobbyesp.spowlo.features.spotify.data.remote.SpotifyServiceImpl
import com.bobbyesp.spowlo.features.spotify.data.remote.search.SpotifySearchServiceImpl
import com.bobbyesp.spowlo.features.spotify.domain.services.SpotifyService
import com.bobbyesp.spowlo.features.spotify.domain.services.search.SpotifySearchService
import org.koin.core.qualifier.named
import org.koin.dsl.module

val spotifyModule = module {
    single(named("client_id")) { BuildConfig.CLIENT_ID }
    single(named("client_secret")) { BuildConfig.CLIENT_SECRET }
    single<SpotifyService> { SpotifyServiceImpl() }
    single<SpotifySearchService> { SpotifySearchServiceImpl() }
}