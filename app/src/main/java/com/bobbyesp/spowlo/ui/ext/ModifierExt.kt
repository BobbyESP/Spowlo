package com.bobbyesp.spowlo.ui.ext

import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import com.bobbyesp.spowlo.ui.common.LocalPlayerAwareWindowInsets

/**
 * A [Modifier] that adds padding to the bottom of the content to be sure that the visible page by the user is not behind the player.
 */
@Composable
fun Modifier.playerSafePadding(): Modifier = this.playerSafePaddingModifier()

@Composable
private fun Modifier.playerSafePaddingModifier(): Modifier {
    val bottomInsetsAsPadding =
        LocalPlayerAwareWindowInsets.current.asPaddingValues()
    val layoutDirection = LocalLayoutDirection.current

    return this.padding(
        bottom = bottomInsetsAsPadding.calculateBottomPadding(),
        start = bottomInsetsAsPadding.calculateStartPadding(
            layoutDirection
        )
    )
}