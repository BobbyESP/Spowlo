package com.bobbyesp.spowlo.utils

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.core.os.LocaleListCompat
import com.bobbyesp.spowlo.App
import com.bobbyesp.spowlo.App.Companion.applicationScope
import com.bobbyesp.spowlo.App.Companion.isFDroidBuild
import com.bobbyesp.spowlo.R
import com.bobbyesp.spowlo.database.CommandTemplate
import com.bobbyesp.spowlo.database.CookieProfile
import com.bobbyesp.spowlo.ui.pages.settings.about.LocalAsset
import com.bobbyesp.spowlo.ui.theme.DEFAULT_SEED_COLOR
import com.google.android.material.color.DynamicColors
import com.kyant.monet.PaletteStyle
import com.tencent.mmkv.MMKV
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

const val CUSTOM_COMMAND = "custom_command"
const val SPOTDL = "spotDL_Init"
const val DEBUG = "debug"
const val CONFIGURE = "configure"
const val DARK_THEME_VALUE = "dark_theme_value"
const val AUDIO_FORMAT = "audio_format"
const val AUDIO_QUALITY = "audio_quality"
const val WELCOME_DIALOG = "welcome_dialog"
const val AUDIO_DIRECTORY = "audio_dir"
const val ORIGINAL_AUDIO = "original_audio"
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

const val USE_YT_METADATA = "use_yt_metadata"
const val USE_SPOTIFY_CREDENTIALS = "use_spotify_credentials"
const val SYNCED_LYRICS = "synced_lyrics"

const val SPOTIFY_CLIENT_ID = "spotify_client_id"
const val SPOTIFY_CLIENT_SECRET = "spotify_client_secret"

const val USE_CACHING = "use_caching"
const val DONT_FILTER_RESULTS = "dont_filter_results"

const val AUDIO_PROVIDER = "audio_provider"

const val TEMPLATE_ID = "template_id"
const val MAX_FILE_SIZE = "max_file_size"
const val COOKIES = "cookies"
const val AUTO_UPDATE = "auto_update"
const val UPDATE_CHANNEL = "update_channel"
const val PRIVATE_MODE = "private_mode"
private const val DYNAMIC_COLOR = "dynamic_color"
const val CELLULAR_DOWNLOAD = "cellular_download"
private const val HIGH_CONTRAST = "high_contrast"
const val FORMAT_SELECTION = "format_selection"

const val SYSTEM_DEFAULT = 0

//UPDATE CHANNELS
const val STABLE = 0
const val PRE_RELEASE = 1

const val TEMPLATE_EXAMPLE = """--audio youtube-music --dont-filter-results"""

private val StringPreferenceDefaults =
    mapOf(
        OUTPUT_PATH_TEMPLATE to "{artists} - {title}.{output-ext}",
        SPOTIFY_CLIENT_ID to "",
        SPOTIFY_CLIENT_SECRET to "",
    )

private val BooleanPreferenceDefaults =
    mapOf(
        FORMAT_SELECTION to true,
        CONFIGURE to true,
        CELLULAR_DOWNLOAD to true,
        ORIGINAL_AUDIO to false,
        USE_SPOTIFY_CREDENTIALS to false,
        USE_YT_METADATA to false,
        SYNCED_LYRICS to false,
        AUTO_UPDATE to true,
        USE_CACHING to true,
        DONT_FILTER_RESULTS to false,
    )

private val IntPreferenceDefaults = mapOf(
    TEMPLATE_ID to 0,
    LANGUAGE to SYSTEM_DEFAULT,
    PALETTE_STYLE to 0,
    DARK_THEME_VALUE to DarkThemePreference.FOLLOW_SYSTEM,
    WELCOME_DIALOG to 1,
    AUDIO_FORMAT to 5,
    AUDIO_QUALITY to 17,
    AUDIO_PROVIDER to 0,
    UPDATE_CHANNEL to STABLE,
)

val palettesMap = mapOf(
    0 to PaletteStyle.TonalSpot,
    1 to PaletteStyle.Spritz,
    2 to PaletteStyle.FruitSalad,
    3 to PaletteStyle.Vibrant,
    4 to PaletteStyle.Spotify,
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

    fun getAudioProvider(): Int = AUDIO_PROVIDER.getInt()

    fun getAudioQuality(): Int = AUDIO_QUALITY.getInt()

    fun getAudioFormatDesc(audioQualityStr: Int = getAudioFormat()): String {
        return when (audioQualityStr) {
            0 -> "mp3"
            1 -> "flac"
            2 -> "ogg"
            3 -> "opus"
            4 -> "m4a"
            5 -> "Default"
            else -> "mp3"
        }
    }

    fun getAudioProviderDesc(audioProviderInt: Int = getAudioProvider()): String {
        return when (audioProviderInt){
            0 -> "youtube-music"
            1 -> "youtube"
            else -> "youtube-music"
        }
    }

    @Composable
    fun getAudioProviderIcon(audioProviderInt: Int = getAudioProvider()): ImageVector {
        return when (audioProviderInt){
            0 -> LocalAsset(id = R.drawable.youtube_music_icons8)
            1 -> LocalAsset(id = R.drawable.icons8_youtube)
            else -> LocalAsset(id = R.drawable.youtube_music_icons8)
        }
    }

    fun getAudioQualityDesc(audioQualityStr: Int = getAudioQuality()): String {
        return when (audioQualityStr) {
            0 -> "8k"
            1 -> "16k"
            2 -> "24k"
            3 -> "32k"
            4 -> "40k"
            5 -> "48k"
            6 -> "64k"
            7 -> "80k"
            8 -> "96k"
            9 -> "112k"
            10 -> "128k"
            11 -> "160k"
            12 -> "192k"
            13 -> "224k"
            14 -> "256k"
            15 -> "320k"
            16 -> "disable"
            17 -> "auto"
            else -> "auto"
        }
    }

    fun isNetworkAvailableForDownload() =
        CELLULAR_DOWNLOAD.getBoolean() || !App.connectivityManager.isActiveNetworkMetered

    fun isAutoUpdateEnabled() = AUTO_UPDATE.getBoolean(!isFDroidBuild())


    fun getLanguageConfiguration(languageNumber: Int = kv.decodeInt(LANGUAGE)) =
        languageMap.getOrElse(languageNumber) { "" }


    private fun getLanguageNumberByCode(languageCode: String): Int =
        languageMap.entries.find { it.value == languageCode }?.key ?: SYSTEM_DEFAULT


    fun getLanguageNumber(): Int {
        return if (Build.VERSION.SDK_INT >= 33)
            getLanguageNumberByCode(
                LocaleListCompat.getAdjustedDefault()[0]?.toLanguageTag().toString()
            )
        else LANGUAGE.getInt()
    }
    data class AppSettings(
        val darkTheme: DarkThemePreference = DarkThemePreference(),
        val isDynamicColorEnabled: Boolean = false,
        val seedColor: Int = DEFAULT_SEED_COLOR,
        val paletteStyleIndex: Int = 0
    )

    private val mutableAppSettingsStateFlow = MutableStateFlow(
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

    fun switchDynamicColor(enabled: Boolean = !mutableAppSettingsStateFlow.value.isDynamicColorEnabled) {
        applicationScope.launch(Dispatchers.IO) {
            mutableAppSettingsStateFlow.update {
                it.copy(isDynamicColorEnabled = enabled)
            }
            kv.encode(DYNAMIC_COLOR, enabled)
        }
    }

    //Cookies
    private const val COOKIE_HEADER = "# Netscape HTTP Cookie File\n" +
            "# Auto-generated by Spowlo built-in WebView\n"

    private val cookiesStateFlow: StateFlow<String> =
        DatabaseUtil.getCookiesFlow().distinctUntilChanged().map {
            it.fold(StringBuilder(COOKIE_HEADER)) { acc: StringBuilder, cookieProfile: CookieProfile ->
                acc.append(cookieProfile.content)
            }.toString()
        }.stateIn(applicationScope, started = SharingStarted.Eagerly, COOKIE_HEADER)

    fun getCookies(): String = cookiesStateFlow.value

    val templateStateFlow: StateFlow<List<CommandTemplate>> =
        DatabaseUtil.getTemplateFlow().distinctUntilChanged().stateIn(
            applicationScope, started = SharingStarted.Eagerly, emptyList()
        )

    fun getTemplate(): CommandTemplate {
        return templateStateFlow.value.run {
            find { it.id == TEMPLATE_ID.getInt() } ?: first()
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