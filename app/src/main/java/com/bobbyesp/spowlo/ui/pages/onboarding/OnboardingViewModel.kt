package com.bobbyesp.spowlo.ui.pages.onboarding

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor() : ViewModel() {
    private val mutablePageViewState = MutableStateFlow(PageViewState())
    val pageViewState = mutablePageViewState.asStateFlow()

    data class PageViewState(
        val pageStage: OnboardingStep = OnboardingStep.MAIN,
    )
}

enum class OnboardingStep {
    MAIN
}

data class OnboardingPageData(
    val index: Int,

    )