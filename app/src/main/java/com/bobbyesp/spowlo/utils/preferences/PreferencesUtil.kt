package com.bobbyesp.spowlo.utils.preferences

import com.bobbyesp.spowlo.ui.theme.DEFAULT_SEED_COLOR
import com.bobbyesp.spowlo.utils.preferences.PreferencesStrings.DARK_THEME_VALUE
import com.bobbyesp.spowlo.utils.preferences.PreferencesStrings.DYNAMIC_COLOR
import com.bobbyesp.spowlo.utils.preferences.PreferencesStrings.HIGH_CONTRAST
import com.bobbyesp.spowlo.utils.preferences.PreferencesStrings.PALETTE_STYLE
import com.bobbyesp.spowlo.utils.preferences.PreferencesStrings.STOP_AFTER_CLOSING_BS
import com.bobbyesp.spowlo.utils.preferences.PreferencesStrings.THEME_COLOR
import com.bobbyesp.spowlo.utils.preferences.PreferencesStrings.THREADS
import com.bobbyesp.spowlo.utils.preferences.PreferencesStrings.UPDATE_CHANNEL
import com.bobbyesp.spowlo.utils.theme.DarkThemePreference
import com.google.android.material.color.DynamicColors
import com.tencent.mmkv.MMKV
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

const val SYSTEM_DEFAULT = 0

//UPDATE CHANNELS
const val STABLE = 0
const val PRE_RELEASE = 1

private val StringPreferenceDefaults: Map<String, String> =
    mapOf()

private val BooleanPreferenceDefaults: Map<String, Boolean> =
    mapOf(
        STOP_AFTER_CLOSING_BS to false,
    )

private val IntPreferenceDefaults: Map<String, Int> =
    mapOf(
        UPDATE_CHANNEL to STABLE,
        DARK_THEME_VALUE to DarkThemePreference.FOLLOW_SYSTEM,
        THREADS to 1,
    )

object PreferencesUtil {
    val kv: MMKV = MMKV.defaultMMKV()

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