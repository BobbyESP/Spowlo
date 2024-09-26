package com.bobbyesp.spowlo.di

import com.bobbyesp.spowlo.features.spotify.auth.CredentialsStorer
import org.koin.dsl.module

val appModules = module {
    single<CredentialsStorer> { CredentialsStorer }
}