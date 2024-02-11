package com.bobbyesp.spowlo.features.inapp_notifications.domain

import com.bobbyesp.spowlo.features.inapp_notifications.domain.model.Notification
import kotlinx.coroutines.flow.MutableStateFlow

interface NotificationManager {
    fun showNotification(notification: Notification)
    fun getCurrentNotification(): MutableStateFlow<Notification?>
    fun showNotification(notificationId: Int)
    fun showLatestNotificationByTimestamp()
    fun dismissNotification()
    fun getNotification(notificationId: Int): Notification?
    fun getSessionNotifications(): List<Notification>
    fun getNotificationsSnapshot(): Map<Int, Notification>
    fun getNotificationMapFlow(): MutableMap<Int, Notification>
}