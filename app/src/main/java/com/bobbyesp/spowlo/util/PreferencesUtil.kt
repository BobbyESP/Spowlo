package com.bobbyesp.spowlo.util

import android.os.Build
import android.util.Log
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.core.os.LocaleListCompat
import com.bobbyesp.spowlo.R
import com.bobbyesp.spowlo.Spowlo.Companion.applicationScope
import com.bobbyesp.spowlo.presentation.ui.theme.ColorScheme.DEFAULT_SEED_COLOR
import com.tencent.mmkv.MMKV
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

object PreferencesUtil {

    private val kv = MMKV.defaultMMKV()

    fun updateValue(key: String, b: Boolean) = kv.encode(key, b)
    fun updateInt(key: String, int: Int) = kv.encode(key, int)
    fun getInt(key: String, int: Int) = kv.decodeInt(key, int)
    fun getValue(key: String): Boolean = kv.decodeBool(key, false)
    fun getValue(key: String, b: Boolean): Boolean = kv.decodeBool(key, b)
    fun getString(key: String): String? = kv.decodeString(key)

    fun getString(key: String, default: String): String = kv.decodeString(key, default).toString()
    fun updateString(key: String, string: String) = kv.encode(key, string)

    fun containsKey(key: String) = kv.containsKey(key)

    //Configuration fields
    const val DARK_THEME = "dark_theme_value"
    const val HIGH_CONTRAST = "high_contrast"
    const val THEME_COLOR = "theme_color"
    const val DYNAMIC_COLOR = "dynamic_color"
    const val LANGUAGE = "language"
    const val SPOTIFY_URL = "spotify_url"
    const val AUDIO_DIRECTORY = "audio_directory"
    const val TEMPLATE_INDEX = "template_index"
    const val TEMPLATE = "template"

    const val SYSTEM_DEFAULT = 0

    // Do not modify
    private const val SIMPLIFIED_CHINESE = 1
    private const val ENGLISH = 2
    private const val CZECH = 3
    private const val FRENCH = 4
    private const val GERMAN = 5
    private const val NORWEGIAN = 6
    private const val DANISH = 7
    private const val SPANISH = 8
    private const val TURKISH = 9
    private const val UKRAINIAN = 10
    private const val RUSSIAN = 11
    private const val ARABIC = 12
    private const val PERSIAN = 13
    private const val INDONESIAN = 14
    private const val FILIPINO = 15
    private const val ITALIAN = 16
    private const val DUTCH = 17
    private const val PORTUGUESE_BRAZIL = 18
    private const val JAPANESE = 19
    private const val POLISH = 20
    private const val HUNGARIAN = 21
    private const val MALAY = 22
    private const val TRADITIONAL_CHINESE = 23
    private const val VIETNAMESE = 24
    private const val BELARUSIAN = 25
    private const val CROATIAN = 26
    private const val BASQUE = 27
    private const val HINDI = 28
    private const val MALAYALAM = 29

    private val mutableAppSettingsStateFlow = MutableStateFlow(
        AppSettings(
            DarkThemePreference(
                darkThemeValue = kv.decodeInt(
                    DARK_THEME,
                    DarkThemePreference.FOLLOW_SYSTEM
                ), isHighContrastModeEnabled = kv.decodeBool(HIGH_CONTRAST, false)
            ),
            isDynamicColorEnabled = kv.decodeBool(DYNAMIC_COLOR),
            seedColor = kv.decodeInt(THEME_COLOR, DEFAULT_SEED_COLOR)
        )
    )

    data class AppSettings(
        val darkTheme: DarkThemePreference = DarkThemePreference(),
        val isDynamicColorEnabled: Boolean = false,
        val seedColor: Int = DEFAULT_SEED_COLOR
    )

    val AppSettingsStateFlow = mutableAppSettingsStateFlow.asStateFlow()

    fun modifyDarkThemePreference(
        darkThemeValue: Int = AppSettingsStateFlow.value.darkTheme.darkThemeValue,
        isHighContrastModeEnabled: Boolean = AppSettingsStateFlow.value.darkTheme.isHighContrastModeEnabled
    ) {
        applicationScope.launch(Dispatchers.IO) {
            mutableAppSettingsStateFlow.update {
                it.copy(
                    darkTheme = AppSettingsStateFlow.value.darkTheme.copy(
                        darkThemeValue = darkThemeValue,
                        isHighContrastModeEnabled = isHighContrastModeEnabled
                    )
                )
            }
            kv.encode(DARK_THEME, darkThemeValue)
            kv.encode(HIGH_CONTRAST, isHighContrastModeEnabled)
        }
    }

    fun modifyThemeSeedColor(colorArgb: Int) {
        applicationScope.launch(Dispatchers.IO) {
            mutableAppSettingsStateFlow.update {
                it.copy(seedColor = colorArgb)
            }
            kv.encode(THEME_COLOR, colorArgb)
        }
    }

    fun switchDynamicColor(enabled: Boolean = !mutableAppSettingsStateFlow.value.isDynamicColorEnabled) {
        applicationScope.launch(Dispatchers.IO) {
            mutableAppSettingsStateFlow.update {
                it.copy(isDynamicColorEnabled = enabled)
            }
            kv.encode(DYNAMIC_COLOR, enabled)
        }
    }

    // Sorted alphabetically
    val languageMap: Map<Int, String> = mapOf(
        Pair(ENGLISH, "en-US"),
        Pair(SPANISH, "es"),
    )

    fun getLanguageConfiguration(languageNumber: Int = kv.decodeInt(LANGUAGE)): String {
        return if (languageMap.containsKey(languageNumber)) languageMap[languageNumber].toString() else ""
    }

    private fun getLanguageNumberByCode(languageCode: String): Int {
        languageMap.entries.forEach {
            if (it.value == languageCode) return it.key
        }
        return SYSTEM_DEFAULT
    }

    fun getLanguageNumber(): Int {
        return if (Build.VERSION.SDK_INT >= 33)
            getLanguageNumberByCode(
                LocaleListCompat.getAdjustedDefault()[0]?.toLanguageTag().toString()
            )
        else getInt(LANGUAGE, 0)
    }

    @Composable
    fun getLanguageDesc(language: Int = getLanguageNumber()): String{
        return when (language) {
            ENGLISH -> stringResource(id = R.string.english)
            SPANISH -> stringResource(id = R.string.spanish)
            else -> stringResource(id = R.string.system_default)
        }
    }

    data class DarkThemePreference(
        val darkThemeValue: Int = FOLLOW_SYSTEM,
        val isHighContrastModeEnabled: Boolean = false
    ) {
        companion object {
            const val FOLLOW_SYSTEM = 1
            const val ON = 2
            const val OFF = 3
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
    private const val TAG = "PreferencesUtil"
}