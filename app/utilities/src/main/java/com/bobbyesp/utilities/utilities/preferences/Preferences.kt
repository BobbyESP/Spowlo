package com.bobbyesp.utilities.utilities.preferences

import com.bobbyesp.utilities.utilities.DarkThemePreference
import com.bobbyesp.utilities.utilities.preferences.PreferencesKeys.DARK_THEME_VALUE
import com.bobbyesp.utilities.utilities.preferences.PreferencesKeys.DYNAMIC_COLOR
import com.bobbyesp.utilities.utilities.preferences.PreferencesKeys.HIGH_CONTRAST
import com.bobbyesp.utilities.utilities.preferences.PreferencesKeys.MMKV_PREFERENCES_NAME
import com.bobbyesp.utilities.utilities.preferences.PreferencesKeys.PALETTE_STYLE
import com.bobbyesp.utilities.utilities.preferences.PreferencesKeys.THEME_COLOR
import com.bobbyesp.utilities.utilities.ui.DEFAULT_SEED_COLOR
import com.google.android.material.color.DynamicColors
import com.tencent.mmkv.MMKV
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

private val StringPreferenceDefaults: Map<String, String> =
    mapOf()

private val BooleanPreferenceDefaults: Map<String, Boolean> =
    mapOf(
    )

private val IntPreferenceDefaults: Map<String, Int> =
    mapOf(
        DARK_THEME_VALUE to DarkThemePreference.FOLLOW_SYSTEM,
    )

object Preferences {
    val kv: MMKV = MMKV.mmkvWithID(MMKV_PREFERENCES_NAME)

    fun String.getInt(default: Int = IntPreferenceDefaults.getOrElse(this) { 0 }): Int =
        kv.decodeInt(this, default)

    fun String.getString(default: String = StringPreferenceDefaults.getOrElse(this) { "" }): String =
        kv.decodeString(this) ?: default

    fun String.getBoolean(default: Boolean = BooleanPreferenceDefaults.getOrElse(this) { false }): Boolean =
        kv.decodeBool(this, default)

    fun String.updateString(newString: String) = kv.encode(this, newString)

    fun String.updateInt(newInt: Int) = kv.encode(this, newInt)

    fun String.updateBoolean(newValue: Boolean) = kv.encode(this, newValue)
    fun updateValue(key: String, b: Boolean) = key.updateBoolean(b)
    fun encodeInt(key: String, int: Int) = key.updateInt(int)
    fun getValue(key: String): Boolean = key.getBoolean()
    fun encodeString(key: String, string: String) = key.updateString(string)
    fun containsKey(key: String) = kv.containsKey(key)

    data class AppSettings(
        val darkTheme: DarkThemePreference = DarkThemePreference(),
        val isDynamicColorEnabled: Boolean = false,
        val seedColor: Int = DEFAULT_SEED_COLOR,
        val paletteStyleIndex: Int = 0
    )

    val mutableAppSettingsStateFlow = MutableStateFlow(
        AppSettings(
            DarkThemePreference(
                darkThemeValue = kv.decodeInt(
                    DARK_THEME_VALUE,
                    DarkThemePreference.FOLLOW_SYSTEM
                ), isHighContrastModeEnabled = kv.decodeBool(HIGH_CONTRAST, false)
            ),
            isDynamicColorEnabled = kv.decodeBool(
                DYNAMIC_COLOR,
                DynamicColors.isDynamicColorAvailable()
            ),
            seedColor = kv.decodeInt(THEME_COLOR, DEFAULT_SEED_COLOR),
            paletteStyleIndex = kv.decodeInt(PALETTE_STYLE, 0)
        )
    )
    val AppSettingsStateFlow = mutableAppSettingsStateFlow.asStateFlow()
}