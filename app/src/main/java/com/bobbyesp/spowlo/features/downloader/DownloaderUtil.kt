package com.bobbyesp.spowlo.features.downloader

import com.bobbyesp.spowlo.utils.preferences.PreferencesStrings.THREADS
import com.bobbyesp.spowlo.utils.preferences.PreferencesUtil
import com.bobbyesp.spowlo.utils.preferences.PreferencesUtil.getInt

object DownloaderUtil {

    val pref = PreferencesUtil

    data class DownloaderPreferences(
        val threads: Int = THREADS.getInt(),
    )
}