package com.bobbyesp.spowlo.ui.common

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.vector.ImageVector
import com.bobbyesp.spowlo.App
import com.bobbyesp.spowlo.R

sealed class Route(
    val route: String,
    val title: String,
    val icon: ImageVector? = null,
) {
    object OnboardingPage : Route("onboarding_page", getStringWithContext(R.string.onboarding))
}

fun getStringWithContext(
    @StringRes resId: Int,
): String {
    return App.context.getString(resId)
}
