package com.bobbyesp.spowlo.utils

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.bobbyesp.spowlo.R
import com.tencent.mmkv.MMKV

const val CUSTOM_COMMAND = "custom_command"
const val EXTRACT_AUDIO = "extract_audio"
const val THUMBNAIL = "create_thumbnail"
const val SPOTDL = "spotDL_Init"
const val DEBUG = "debug"
const val CONFIGURE = "configure"
const val DARK_THEME_VALUE = "dark_theme_value"
const val AUDIO_FORMAT = "audio_format"
const val WELCOME_DIALOG = "welcome_dialog"
const val AUDIO_DIRECTORY = "audio_dir"
const val SDCARD_DOWNLOAD = "sdcard_download"
const val SDCARD_URI = "sd_card_uri"
const val SUBDIRECTORY = "sub-directory"
const val PLAYLIST = "playlist"
const val LANGUAGE = "language"
const val NOTIFICATION = "notification"
private const val THEME_COLOR = "theme_color"
const val PALETTE_STYLE = "palette_style"
const val CUSTOM_PATH = "custom_path"
const val OUTPUT_PATH_TEMPLATE = "path_template"

const val TEMPLATE_ID = "template_id"
const val MAX_FILE_SIZE = "max_file_size"
const val COOKIES = "cookies"
const val AUTO_UPDATE = "auto_update"
const val PRIVATE_MODE = "private_mode"
private const val DYNAMIC_COLOR = "dynamic_color"
const val CELLULAR_DOWNLOAD = "cellular_download"
private const val HIGH_CONTRAST = "high_contrast"
const val FORMAT_SELECTION = "format_selection"

const val SYSTEM_DEFAULT = 0

private val StringPreferenceDefaults =
    mapOf(
        OUTPUT_PATH_TEMPLATE to "{artists} - {title}.{output-ext}",
    )

private val BooleanPreferenceDefaults =
    mapOf(
        FORMAT_SELECTION to true,
        CONFIGURE to true,
        CELLULAR_DOWNLOAD to true
    )

private val IntPreferenceDefaults = mapOf(
    TEMPLATE_ID to 0,
    LANGUAGE to SYSTEM_DEFAULT,
    PALETTE_STYLE to 0,
    DARK_THEME_VALUE to DarkThemePreference.FOLLOW_SYSTEM,
    WELCOME_DIALOG to 1,
    AUDIO_FORMAT to 0,
)

object PreferencesUtil {
    private val kv = MMKV.defaultMMKV()

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

    fun getOutputPathTemplate(): String = OUTPUT_PATH_TEMPLATE.getString()

    fun getAudioFormat(): Int = AUDIO_FORMAT.getInt()

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