package com.bobbyesp.utilities

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.ContextCompat.startActivity

object Share {
    fun shareText(context: Context, text: String) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, text)
        }
        startActivity(context, intent, null)
    }

    fun shareImage(context: Context, uri: Uri) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "image/*"
            putExtra(Intent.EXTRA_STREAM, uri)
        }
        startActivity(context, intent, null)
    }

    fun shareImage(context: Context, uri: Uri, text: String) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "image/*"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_TEXT, text)
        }
        startActivity(context, intent, null)
    }

    fun shareHtml(context: Context, html: String) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/html"
            putExtra(Intent.EXTRA_TEXT, html)
        }
        startActivity(context, intent, null)
    }

    fun shareFile(context: Context, uri: Uri) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "application/*"
            putExtra(Intent.EXTRA_STREAM, uri)
        }
        startActivity(context, intent, null)
    }
}