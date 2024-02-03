package com.bobbyesp.spowlo.utils.services

import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import com.bobbyesp.spowlo.MainActivity

class DownloaderKeepUpService : Service() {
    override fun onBind(p0: Intent?): IBinder {
        val pendingIntent: PendingIntent =
            Intent(this, MainActivity::class.java).let { notificationIntent ->
                PendingIntent.getActivity(
                    this, 0, notificationIntent,
                    PendingIntent.FLAG_IMMUTABLE
                )
            }

        return DownloadServiceBinder()
    }

    override fun onUnbind(intent: Intent?): Boolean {
        Log.d("DownloaderKeepUpService", "onUnbind: ")
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
        return super.onUnbind(intent)
    }

    inner class DownloadServiceBinder : Binder() {
        fun getService(): DownloaderKeepUpService = this@DownloaderKeepUpService
    }
}