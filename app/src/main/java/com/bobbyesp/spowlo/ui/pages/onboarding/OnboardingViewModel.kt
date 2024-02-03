package com.bobbyesp.spowlo.ui.pages.onboarding

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor() : ViewModel() {

}

enum class OnboardingStep {
    WELCOME, DONE
}