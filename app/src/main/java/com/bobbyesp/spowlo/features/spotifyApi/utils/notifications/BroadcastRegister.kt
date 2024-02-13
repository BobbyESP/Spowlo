package com.bobbyesp.spowlo.features.spotifyApi.utils.notifications

import android.content.Context
import android.content.IntentFilter
import android.os.Build
import com.adamratzman.spotify.notifications.AbstractSpotifyBroadcastReceiver
import com.adamratzman.spotify.notifications.SpotifyBroadcastType
import com.bobbyesp.spowlo.MainActivity

fun Context.registerSpBroadcastReceiver(
    receiver: AbstractSpotifyBroadcastReceiver,
    vararg notificationTypes: SpotifyBroadcastType
) {
    val filter = IntentFilter()
    notificationTypes.forEach { filter.addAction(it.id) }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        this.registerReceiver(
            MainActivity.spotifyBroadcastReceiver,
            filter,
            Context.RECEIVER_EXPORTED
        )
    } else {
        this.registerReceiver(receiver, filter)
    }
}
