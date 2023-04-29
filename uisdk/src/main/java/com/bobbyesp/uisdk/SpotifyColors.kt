package com.bobbyesp.uisdk


import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import kotlinx.collections.immutable.persistentMapOf

object SpotifyColors{
    private val registeredThemes = persistentMapOf(
        "Default" to ColorTheme( //TODO: Add themes
            gradientBackground = Color(0xFF1A1A1A),
            colorShowcaseHeader = Color(0xFF1A1A1A),
            gradientShowcaseHeaderLeft = Color(0xFF1A1A1A),
            btnBackground = Color(0xFF1A1A1A),
            btnOutline = Color(0xFF1A1A1A),
        )
    )
    fun getColorTheme(id: String?) = id?.let { registeredThemes[it] } ?: default()
    fun default() = registeredThemes["Default"]!!
    @Immutable
    class ColorTheme(
        val gradientBackground: Color,
        val colorShowcaseHeader: Color,
        val gradientShowcaseHeaderLeft: Color,
        val btnBackground: Color,
        val btnOutline: Color,
    )

    private fun alpha(prcnt: Float): Int = (prcnt * 255f).toInt()
}