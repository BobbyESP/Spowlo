package com.bobbyesp.spowlo.utils

import NotificationActionReceiver
import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationChannelGroup
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE
import com.bobbyesp.spowlo.R

private const val TAG = "NotificationsUtil"

@SuppressLint("StaticFieldLeak")
object NotificationsUtil {
    private lateinit var notificationManager: NotificationManager
    private const val PROGRESS_MAX = 100
    private const val PROGRESS_INITIAL = 0
    private const val CHANNEL_ID = "download_notification"
    private const val SERVICE_CHANNEL_ID = "download_service"
    private const val NOTIFICATION_GROUP_ID = "spowlo.download.notification"
    private const val DEFAULT_NOTIFICATION_ID = 100
    const val SERVICE_NOTIFICATION_ID = 123
    private lateinit var serviceNotification: Notification
    private val commandNotificationBuilder = { context: Context ->
        NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_monochrome)
    }

    fun init(context: Context) {
        notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun createNotificationChannel(context: Context) {
        val name = context.getString(R.string.channel_name)
        val serviceChannelName = context.getString(R.string.service_channel_name)
        val descriptionText = context.getString(R.string.channel_description)
        val importance = NotificationManager.IMPORTANCE_LOW
        val channelGroup =
            NotificationChannelGroup(NOTIFICATION_GROUP_ID, context.getString(R.string.download))
        val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
            description = descriptionText
            group = NOTIFICATION_GROUP_ID
        }
        val serviceChannel =
            NotificationChannel(SERVICE_CHANNEL_ID, serviceChannelName, importance).apply {
                description = context.getString(R.string.service_title)
                group = NOTIFICATION_GROUP_ID
            }
        notificationManager.createNotificationChannelGroup(channelGroup)
        notificationManager.createNotificationChannel(channel)
        notificationManager.createNotificationChannel(serviceChannel)
    }

    fun notifyProgress(
        context: Context,
        title: String,
        notificationId: Int = DEFAULT_NOTIFICATION_ID,
        progress: Int = PROGRESS_INITIAL,
        taskId: String? = null,
        text: String? = null
    ) {
        if (!PreferencesUtil.getValue(NOTIFICATION)) return

        val pendingIntent = taskId?.let {
            Intent(context.applicationContext, NotificationActionReceiver::class.java)
                .putExtra(NotificationActionReceiver.TASK_ID, taskId)
                .putExtra(NotificationActionReceiver.NOTIFICATION_ID, notificationId)
                .putExtra(NotificationActionReceiver.ACTION, NotificationActionReceiver.ActionType.CANCEL_TASK.ordinal).run {
                    PendingIntent.getBroadcast(
                        context.applicationContext,
                        notificationId,
                        this,
                        PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
                    )
                }
        }

        commandNotificationBuilder(context)
            .setContentTitle(title)
            .setProgress(PROGRESS_MAX, progress, progress <= 0)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setStyle(NotificationCompat.BigTextStyle().bigText(text))
            .run {
                pendingIntent?.let {
                    addAction(
                        R.drawable.outline_cancel_24,
                        context.getString(R.string.cancel),
                        it
                    )
                }
                notificationManager.notify(notificationId, build())
            }
    }

    fun finishNotification(
        context: Context,
        notificationId: Int = DEFAULT_NOTIFICATION_ID,
        title: String? = null,
        text: String? = null,
        intent: PendingIntent? = null,
    ) {
        if (!PreferencesUtil.getValue(NOTIFICATION)) return

        Log.d(TAG, "finishNotification: ")
        notificationManager.cancel(notificationId)
        val builder = commandNotificationBuilder(context)
            .setContentTitle(title)
            .setOngoing(false)
            .setAutoCancel(true)

        text?.let {
            builder.setStyle(NotificationCompat.BigTextStyle().bigText(text))
        }

        intent?.let { builder.setContentIntent(intent) }
        notificationManager.notify(notificationId, builder.build())
    }

    fun finishNotificationForParallelDownloads(
        context: Context,
        notificationId: Int = DEFAULT_NOTIFICATION_ID,
        title: String? = null,
        text: String? = null,
    ) {
        if (!PreferencesUtil.getValue(NOTIFICATION)) return

        notificationManager.cancel(notificationId)
        val builder =
            commandNotificationBuilder(context)
                .setContentText(text)
                .setProgress(0, 0, false)
                .setAutoCancel(true)
                .setOngoing(false)
                .setStyle(null)
        title?.let { builder.setContentTitle(title) }

        notificationManager.notify(notificationId, builder.build())
    }

    fun makeServiceNotification(context: Context, intent: PendingIntent): Notification {
        serviceNotification = NotificationCompat.Builder(context, SERVICE_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_monochrome)
            .setContentTitle(context.getString(R.string.app_name))
            .setContentText(context.getString(R.string.service_title))
            .setOngoing(true)
            .setContentIntent(intent)
            .setForegroundServiceBehavior(FOREGROUND_SERVICE_IMMEDIATE)
            .build()
        return serviceNotification
    }

    fun updateServiceNotification(context: Context, index: Int, itemCount: Int) {
        if (!PreferencesUtil.getValue(NOTIFICATION)) return

        serviceNotification = NotificationCompat.Builder(context, serviceNotification)
            .setContentTitle(context.getString(R.string.service_title) + " ($index/$itemCount)")
            .build()
        notificationManager.notify(SERVICE_NOTIFICATION_ID, serviceNotification)
    }

    fun cancelNotification(notificationId: Int, context: Context) {
        notificationManager.cancel(notificationId)
    }

    fun cancelAllNotifications() {
        notificationManager.cancelAll()
    }

    fun makeErrorReportNotification(
        context: Context,
        title: String = context.getString(R.string.download_error_msg),
        notificationId: Int,
        error: String,
    ) {
        if (!PreferencesUtil.getValue(NOTIFICATION)) return

        val intent = Intent(context, NotificationActionReceiver::class.java)
            .putExtra(NotificationActionReceiver.NOTIFICATION_ID, notificationId)
            .putExtra(NotificationActionReceiver.ERROR_REPORT, error)
            .putExtra(NotificationActionReceiver.ACTION, NotificationActionReceiver.ActionType.ERROR_REPORT.ordinal)

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            notificationId,
            intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        commandNotificationBuilder(context)
            .setContentTitle(title)
            .setContentText(error)
            .setOngoing(false)
            .addAction(
                R.drawable.outline_content_copy_24,
                context.getString(R.string.copy_error_report),
                pendingIntent
            ).run {
                notificationManager.cancel(notificationId)
                notificationManager.notify(notificationId, build())
            }
    }

    fun makeNotificationForParallelDownloads(
        context: Context,
        notificationId: Int,
        taskId: String,
        progress: Int,
        text: String? = null,
        extraString: String,
        taskUrl: String
    ) {
        if (!PreferencesUtil.getValue(NOTIFICATION)) return

        val intent = Intent(context.applicationContext, NotificationActionReceiver::class.java)
            .putExtra(NotificationActionReceiver.TASK_ID, taskId)
            .putExtra(NotificationActionReceiver.NOTIFICATION_ID, notificationId)
            .putExtra(NotificationActionReceiver.ACTION, NotificationActionReceiver.ActionType.CANCEL_TASK.ordinal)

        val pendingIntent = PendingIntent.getBroadcast(
            context.applicationContext,
            notificationId,
            intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )

        commandNotificationBuilder(context)
            .setContentTitle(extraString)
            .setProgress(PROGRESS_MAX, progress, progress <= 0)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setStyle(NotificationCompat.BigTextStyle().bigText(text))
            .addAction(
                R.drawable.outline_cancel_24,
                context.getString(R.string.cancel),
                pendingIntent
            )
            .run {
                notificationManager.notify(notificationId, build())
            }
    }
}