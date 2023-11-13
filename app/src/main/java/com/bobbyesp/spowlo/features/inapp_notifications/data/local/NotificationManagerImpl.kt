package com.bobbyesp.spowlo.features.inapp_notifications.data.local

import android.util.Log
import com.bobbyesp.spowlo.features.inapp_notifications.domain.model.Notification
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class NotificationManagerImpl : NotificationManager {
    private val _notifications = MutableStateFlow<Map<Int, Notification>>(emptyMap())
    val notifications = _notifications.asStateFlow()

    private val _currentNotification = MutableStateFlow<Notification?>(null)

    override fun showNotification(notification: Notification) {
        //if the notification id is already present, we just make it visible, otherwise we add it
        val existingNotification = getNotification(notification.id)
        if (existingNotification == null) {
            Log.i("NotificationManager", "Showing notification ${notification.id}")
            _currentNotification.value = notification
            _notifications.update {
                it + (notification.id to notification)
            }
        } else {
            //in the value of the list, we locate the notification and we make it visible by using .copy(visible = true)
            _notifications.update {
                it + (notification.id to existingNotification)
            }
        }
    }

    override fun showNotification(notificationId: Int) {
        getNotification(notificationId)?.let {
            showNotification(it)
        } ?: throw Exception("Notification with id $notificationId not found")
    }

    override fun getCurrentNotification(): MutableStateFlow<Notification?> {
        return _currentNotification
    }

    override fun showLatestNotificationByTimestamp() {
        val latestNotification: Notification? = _notifications.value.values.maxByOrNull { it.timestamp }
        latestNotification?.let {
            showNotification(it)
        }
    }

    override fun dismissNotification() {
        _currentNotification.value = null
    }
    override fun getNotification(notificationId: Int): Notification? {
        return _notifications.value[notificationId]
    }

    override fun getSessionNotifications(): List<Notification> {
        return _notifications.value.values.toList()
    }

    override fun getNotificationMapFlow(): MutableStateFlow<Map<Int, Notification>> {
        return _notifications
    }
}