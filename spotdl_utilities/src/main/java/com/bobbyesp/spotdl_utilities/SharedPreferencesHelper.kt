package com.bobbyesp.spotdl_utilities

import android.content.Context

class SharedPreferencesHelper {
    fun update(appContext: Context, key: String?, value: String?) {
        val pref = appContext.getSharedPreferences(sharedPrefsName, Context.MODE_PRIVATE)
        val editor = pref.edit()
        editor.putString(key, value)
        editor.apply()
    }

    operator fun get(appContext: Context, key: String?): String? {
        val pref = appContext.getSharedPreferences(sharedPrefsName, Context.MODE_PRIVATE)
        return pref.getString(key, null)
    }

    companion object {
        private const val sharedPrefsName = "spotdl-android"
    }
}