package com.bobbyesp.spowlo.util

import android.content.pm.PackageManager
import android.util.Log
import com.bobbyesp.spowlo.Spowlo

object VersionsUtil {

    //Spotify market package name
    private var packageName: String = ""

    fun getSpotifyVersion(type: String): String {

        val pm = Spowlo.context.packageManager

        when(type){
            "regular" -> packageName = "com.spotify.music"

            "cloned" -> packageName = "com.spotify.musix"
        }
        return try {
            val packageInfo = pm.getPackageInfo(packageName, 0)
            val versionName = packageInfo.versionName
            val versionCode = packageInfo.versionCode
            val version = "$versionName ($versionCode)"

            versionName
        } catch (e: Exception) {
            Log.e("VersionsUtil", "getSpotifyVersion: ${e.message}")
            "Not installed"
        }
    }
}