package com.bobbyesp.spowlo.features.spotify.data.local.broadcast

import android.content.Context
import android.content.IntentFilter
import android.os.Build
import com.adamratzman.spotify.notifications.AbstractSpotifyBroadcastReceiver
import com.adamratzman.spotify.notifications.SpotifyBroadcastType

fun Context.registerSpotifyBroadcastReceiver(
    receiver: AbstractSpotifyBroadcastReceiver,
    vararg notificationTypes: SpotifyBroadcastType
) {
    val filter = IntentFilter()
    notificationTypes.forEach { filter.addAction(it.id) }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        this.registerReceiver(
            receiver,
            filter,
            Context.RECEIVER_EXPORTED
        )
    } else {
        this.registerReceiver(receiver, filter)
    }
}