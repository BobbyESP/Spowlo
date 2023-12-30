package com.bobbyesp.spowlo.features.notification_manager.data.local

import android.util.Log
import com.bobbyesp.spowlo.features.notification_manager.domain.model.Notification
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class NotificationManagerImpl : NotificationManager {
    private val _notifications = MutableStateFlow<Map<Int, Notification>>(emptyMap())
    private val notifications = _notifications.asStateFlow()

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
        val latestNotification: Notification? = _notifications.value.values.maxByOrNull { it.timestamp }
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
        return _notifications.value[notificationId]
    }

    /**
     * This method is used to get all the notifications that have been shown in the current session
     * @return a list of notifications
     */
    override fun getSessionNotifications(): List<Notification> {
        return _notifications.value.values.toList()
    }

    /**
     * This method is used to get all the notifications that have been shown in the current session
     * @return a list of notifications
     */
    override fun getNotificationMapFlow(): StateFlow<Map<Int, Notification>> {
        return notifications
    }
}