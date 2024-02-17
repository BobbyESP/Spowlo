package com.bobbyesp.spowlo.ui.components.buttons

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.bobbyesp.spowlo.R

@Composable
fun BackButton(onClick: () -> Unit) {
    IconButton(modifier = Modifier, onClick = onClick) {
        Icon(
            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
            contentDescription = stringResource(R.string.back),
        )
    }
}

@Composable
fun CloseButton(onClick: () -> Unit) {
    IconButton(modifier = Modifier, onClick = onClick) {
        Icon(
            imageVector = Icons.Rounded.Close,
            contentDescription = stringResource(R.string.back),
        )
    }
}

//create a dynamic button that depending on a boolean has one Icon and one onClick function or another
@Composable
fun DynamicButton(
    modifier: Modifier = Modifier,
    icon: @Composable () -> Unit,
    icon2: @Composable () -> Unit,
    isIcon1: Boolean,
) {
    IconButton(modifier = modifier, onClick = { }) {
        if (isIcon1) {
            icon()
        } else {
            icon2()
        }
    }
}