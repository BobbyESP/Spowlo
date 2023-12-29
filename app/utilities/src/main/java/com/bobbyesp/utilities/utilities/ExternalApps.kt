package com.bobbyesp.utilities.utilities

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import androidx.core.net.toUri

object ExternalApps {
    object Icons {
        fun getIconForIntent(context: Context, intent: Intent): Drawable? {
            return try {
                val icon = context.packageManager.getActivityIcon(intent)
                icon
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }

        fun getIconForPackageName(context: Context, packageName: String): Drawable? {
            return try {
                val icon = context.packageManager.getApplicationIcon(packageName)
                icon
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }

        fun getBrowserIconForUrl(context: Context, url: String): Drawable? {
            return try {
                val intent = Intent(Intent.ACTION_VIEW, url.toUri())
                val icon = context.packageManager.getActivityIcon(intent)
                icon
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }
}