package com.bobbyesp.spowlo.features.mod_downloader.util

import android.content.Intent
import android.net.Uri
import com.bobbyesp.spowlo.App

object ModDownloaderUtils {
    fun openLinkInBrowser(link: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(link)).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        App.context.startActivity(intent)
    }
}