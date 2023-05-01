package com.bobbyesp.appmodules.hub.ui.components

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import com.bobbyesp.appmodules.hub.ui.dac.LocalDacDelegator
import com.spotify.dac.player.v1.proto.PlayCommand

@Composable
fun DynamicLikeButton(
    objectUrl: String,
    modifier: Modifier = Modifier
) {
    IconButton(onClick = {
        // todo
    }, modifier = modifier) {
        Icon(Icons.Rounded.Favorite, contentDescription = null)
    }
}

@Composable
fun DynamicPlayButton(
    command: PlayCommand,
    modifier: Modifier = Modifier
) {
    val dacDelegate = LocalDacDelegator.current
    FilledIconButton(
        onClick = { dacDelegate.dispatchPlay(command) }, modifier = modifier.clip(CircleShape)
    ) {
        Icon(Icons.Rounded.PlayArrow, contentDescription = null)
    }
}