package com.bobbyesp.spowlo.utils

import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.core.os.LocaleListCompat
import com.bobbyesp.spowlo.R
import com.bobbyesp.spowlo.utils.PreferencesUtil.getInt

private fun getLanguageNumberByCode(languageCode: String): Int =
    languageMap.entries.find { it.value == languageCode }?.key ?: SYSTEM_DEFAULT

fun getLanguageNumber(): Int {
    return if (Build.VERSION.SDK_INT >= 33)
        getLanguageNumberByCode(
            LocaleListCompat.getAdjustedDefault()[0]?.toLanguageTag().toString()
        )
    else LANGUAGE.getInt()
}


@Composable
fun getLanguageDesc(language: Int = getLanguageNumber()): String {
    return stringResource(
        when (language) {
            ENGLISH -> R.string.la_en_US
            SPANISH -> R.string.la_es
            else -> R.string.follow_system
        }
    )
}

// Do not modify
private const val ENGLISH = 1
private const val SPANISH = 2

// Sorted alphabetically
val languageMap: Map<Int, String> = mapOf(
    ENGLISH to "en-US",
    SPANISH to "es",
)