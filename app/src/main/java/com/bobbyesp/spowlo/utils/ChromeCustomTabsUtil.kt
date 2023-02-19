package com.bobbyesp.spowlo.utils

import android.content.Intent
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import com.bobbyesp.spowlo.App

object ChromeCustomTabsUtil {

    fun openUrl(url: String) {
        val builder = CustomTabsIntent.Builder()
        val customTabsIntent = builder.build()
        customTabsIntent.intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        customTabsIntent.launchUrl(App.context, Uri.parse(url))
    }
}