package com.bobbyesp.spowlo.features.inapp_notifications.data.local

import android.util.Log
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.saveable.Saver
import com.bobbyesp.spowlo.features.inapp_notifications.domain.NotificationManager
import com.bobbyesp.spowlo.features.inapp_notifications.domain.model.Notification
import kotlinx.coroutines.flow.MutableStateFlow

class NotificationManagerImpl : NotificationManager {
    init {
        Log.i("NotificationManager", "NotificationManagerImpl created")
    }

    private val _notifications = mutableStateMapOf<Int, Notification>()
    val notifications = _notifications

    private val _currentNotification = MutableStateFlow<Notification?>(null)

    /**
     * This method is used to show a notification in the UI
     * @param notification the notification to show
     */
    override fun showNotification(notification: Notification) {
        //if the notification id is already present, we just make it visible, otherwise we add it
        val existingNotification = getNotification(notification.id)
        if (existingNotification == null) {
            Log.i("NotificationManager", "Showing notification ${notification.id}")
            _currentNotification.value = notification
            _notifications[notification.id] = notification
        } else {
            //in the value of the list, we locate the notification and we make it visible by using .copy(visible = true)
            _notifications[notification.id] = existingNotification
        }
    }

    /**
     * This method is used to show a notification in the UI
     * @param notificationId the id of the notification to show
     */
    override fun showNotification(notificationId: Int) {
        getNotification(notificationId)?.let {
            showNotification(it)
        } ?: throw Exception("Notification with id $notificationId not found")
    }

    /**
     * This method is used to get the current notification
     * @return the current notification
     */
    override fun getCurrentNotification(): MutableStateFlow<Notification?> {
        return _currentNotification
    }

    override fun showLatestNotificationByTimestamp() {
        val latestNotification: Notification? = _notifications.values.maxByOrNull { it.timestamp }
        latestNotification?.let {
            showNotification(it)
        }
    }

    /**
     * This method is used to dismiss the current notification
     */
    override fun dismissNotification() {
        _currentNotification.value = null
    }

    /**
     * This method is used to get a notification by its id
     * @param notificationId the id of the notification to get
     * @return the notification with the given id
     */
    override fun getNotification(notificationId: Int): Notification? {
        return _notifications[notificationId]
    }

    /**
     * This method is used to get all the notifications that have been shown in the current session
     * @return a list of notifications
     */
    override fun getSessionNotifications(): List<Notification> {
        return _notifications.values.toList()
    }

    override fun getNotificationsSnapshot(): Map<Int, Notification> {
        return _notifications
    }

    /**
     * This method is used to get all the notifications that have been shown in the current session
     * @return a list of notifications
     */
    override fun getNotificationMapFlow(): MutableMap<Int, Notification> {
        return notifications
    }
}

fun NotificationManagerSaver(): Saver<NotificationManager, Map<Int, Notification>> { //TODO: CHANGE THIS. CRASHES BECAUSE OF PARCELABLE
    return Saver(
        save = { notificationManager -> notificationManager.getNotificationsSnapshot().toMap() },
        restore = { NotificationManagerImpl().apply { notifications.putAll(it) } }
    )
}