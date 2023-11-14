package com.bobbyesp.spowlo.features.inapp_notifications.data.local

import com.bobbyesp.spowlo.features.inapp_notifications.domain.model.Notification
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

interface NotificationManager {
    fun showNotification(notification: Notification)
    fun getCurrentNotification(): MutableStateFlow<Notification?>
    fun showNotification(notificationId: Int)
    fun showLatestNotificationByTimestamp()
    fun dismissNotification()
    fun getNotification(notificationId: Int): Notification?
    fun getSessionNotifications(): List<Notification>
    fun getNotificationMapFlow(): StateFlow<Map<Int, Notification>>
}