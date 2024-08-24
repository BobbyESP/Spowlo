package com.bobbyesp.spowlo

import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import com.bobbyesp.spowlo.utils.NOTIFICATION
import com.bobbyesp.spowlo.utils.NotificationsUtil
import com.bobbyesp.spowlo.utils.NotificationsUtil.SERVICE_NOTIFICATION_ID
import com.bobbyesp.spowlo.utils.PreferencesUtil

private val TAG = DownloaderKeepUpService::class.java.simpleName

class DownloaderKeepUpService : Service() {
    override fun onBind(intent: Intent): IBinder {
        Log.d(TAG, "onBind: ")
        val pendingIntent: PendingIntent =
            Intent(this, MainActivity::class.java).let { notificationIntent ->
                PendingIntent.getActivity(
                    this, 0, notificationIntent,
                    PendingIntent.FLAG_IMMUTABLE
                )
            }
        if (PreferencesUtil.getValue(NOTIFICATION)) {
            val notification = NotificationsUtil.makeServiceNotification(pendingIntent)
            startForeground(SERVICE_NOTIFICATION_ID, notification)
        }
        return DownloadServiceBinder()
    }


    override fun onUnbind(intent: Intent?): Boolean {
        Log.d(TAG, "onUnbind: ")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            stopForeground(STOP_FOREGROUND_REMOVE)
        } else {
            stopForeground(true)
        }
        stopSelf()
        return super.onUnbind(intent)
    }

    inner class DownloadServiceBinder : Binder() {
        fun getService(): DownloaderKeepUpService = this@DownloaderKeepUpService
    }
}
