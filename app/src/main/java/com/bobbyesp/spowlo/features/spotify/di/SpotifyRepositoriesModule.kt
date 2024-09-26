package com.bobbyesp.spowlo.features.spotify.di

import com.bobbyesp.spowlo.features.spotify.data.repository.SearchRepositoryImpl
import com.bobbyesp.spowlo.features.spotify.domain.repositories.SearchRepository
import org.koin.dsl.module

val spotifyRepositoriesModule = module {
    single<SearchRepository> { SearchRepositoryImpl() }
}