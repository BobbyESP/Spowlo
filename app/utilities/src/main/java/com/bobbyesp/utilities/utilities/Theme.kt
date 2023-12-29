package com.bobbyesp.utilities.utilities

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.bobbyesp.utilities.R
import com.bobbyesp.utilities.utilities.preferences.Preferences
import com.bobbyesp.utilities.utilities.preferences.Preferences.AppSettingsStateFlow
import com.bobbyesp.utilities.utilities.preferences.Preferences.mutableAppSettingsStateFlow
import com.bobbyesp.utilities.utilities.preferences.PreferencesKeys.DARK_THEME_VALUE
import com.bobbyesp.utilities.utilities.preferences.PreferencesKeys.DYNAMIC_COLOR
import com.bobbyesp.utilities.utilities.preferences.PreferencesKeys.HIGH_CONTRAST
import com.bobbyesp.utilities.utilities.preferences.PreferencesKeys.PALETTE_STYLE
import com.bobbyesp.utilities.utilities.preferences.PreferencesKeys.THEME_COLOR
import com.kyant.monet.PaletteStyle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

object Theme {
    lateinit var applicationScope: CoroutineScope

    private val kv = Preferences.kv
    private val appSettingsFlow = AppSettingsStateFlow.value

    val paletteStyles = listOf(
        PaletteStyle.TonalSpot,
        PaletteStyle.Spritz,
        PaletteStyle.FruitSalad,
        PaletteStyle.Vibrant,
        PaletteStyle.Monochrome
    )

    const val STYLE_TONAL_SPOT = 0
    const val STYLE_SPRITZ = 1
    const val STYLE_FRUIT_SALAD = 2
    const val STYLE_VIBRANT = 3
    const val STYLE_MONOCHROME = 4

    fun modifyDarkThemePreference(
        darkThemeValue: Int = appSettingsFlow.darkTheme.darkThemeValue,
        isHighContrastModeEnabled: Boolean = appSettingsFlow.darkTheme.isHighContrastModeEnabled
    ) {
        applicationScope.launch(Dispatchers.IO) {
            mutableAppSettingsStateFlow.update {
                it.copy(
                    darkTheme = appSettingsFlow.darkTheme.copy(
                        darkThemeValue = darkThemeValue,
                        isHighContrastModeEnabled = isHighContrastModeEnabled
                    )
                )
            }
            kv.encode(DARK_THEME_VALUE, darkThemeValue)
            kv.encode(HIGH_CONTRAST, isHighContrastModeEnabled)
        }
    }

    fun modifyThemeSeedColor(colorArgb: Int, paletteStyleIndex: Int) {
        applicationScope.launch(Dispatchers.IO) {
            mutableAppSettingsStateFlow.update {
                it.copy(seedColor = colorArgb, paletteStyleIndex = paletteStyleIndex)
            }
            kv.encode(THEME_COLOR, colorArgb)
            kv.encode(PALETTE_STYLE, paletteStyleIndex)
        }
    }

    fun switchDynamicColor(enabled: Boolean = !appSettingsFlow.isDynamicColorEnabled) {
        applicationScope.launch(Dispatchers.IO) {
            mutableAppSettingsStateFlow.update {
                it.copy(isDynamicColorEnabled = enabled)
            }
            kv.encode(DYNAMIC_COLOR, enabled)
        }
    }
}

data class DarkThemePreference(
    val darkThemeValue: Int = FOLLOW_SYSTEM,
    val isHighContrastModeEnabled: Boolean = false
) {
    companion object {
        const val FOLLOW_SYSTEM = 1
        const val ON = 2
        const val OFF = 3 // Non used
    }

    @Composable
    fun isDarkTheme(): Boolean {
        return if (darkThemeValue == FOLLOW_SYSTEM)
            isSystemInDarkTheme()
        else darkThemeValue == ON
    }

    @Composable
    fun getDarkThemeDesc(): String {
        return when (darkThemeValue) {
            FOLLOW_SYSTEM -> stringResource(R.string.follow_system)
            ON -> stringResource(R.string.on)
            else -> stringResource(R.string.off)
        }
    }
}