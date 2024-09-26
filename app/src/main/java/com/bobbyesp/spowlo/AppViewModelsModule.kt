package com.bobbyesp.spowlo

import com.bobbyesp.spowlo.presentation.pages.auth.SpotifyAuthManagerViewModel
import com.bobbyesp.spowlo.presentation.pages.profile.SpProfilePageViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appViewModels = module {
    viewModel { SpProfilePageViewModel() }
    viewModel { SpotifyAuthManagerViewModel() }
}

