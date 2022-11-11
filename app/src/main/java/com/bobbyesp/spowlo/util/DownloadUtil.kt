package com.bobbyesp.spowlo.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import com.bobbyesp.spowlo.Spowlo
import okhttp3.OkHttpClient
import java.io.File

object DownloadUtil {

    private const val TAG = "DownloadUtil"
    private var apkUrl: String = ""

    private val client = OkHttpClient()

    fun openLinkInBrowser(link: String) {
        //add flag FLAG_ACTIVITY_NEW_TASK to open the browser in a new task
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(link)).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        Spowlo.context.startActivity(intent)
    }

    fun copyLinkToClipboard(link: String) {
        val clipboard = Spowlo.context.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
        val clip = android.content.ClipData.newPlainText("Spotify APK Link", link)
        clipboard.setPrimaryClip(clip)
        //create a toast to show the link has been copied
        Toast.makeText(Spowlo.context, "Link copied to clipboard", Toast.LENGTH_SHORT).show()
    }

    sealed class DownloadStatus {
        object NotYet : DownloadStatus()
        data class Progress(val percent: Int) : DownloadStatus()
        data class Finished(val file: File) : DownloadStatus()
    }
}