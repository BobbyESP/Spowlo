package com.bobbyesp.ui.components.button

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.bobbyesp.ui.R

@Composable
fun BackButton(onClick: () -> Unit) {
    IconButton(modifier = Modifier, onClick = onClick) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = stringResource(R.string.back),
        )
    }
}

@Composable
fun CloseButton(onClick: () -> Unit) {
    IconButton(modifier = Modifier, onClick = onClick) {
        Icon(
            imageVector = Icons.Default.Close,
            contentDescription = stringResource(R.string.back),
        )
    }
}

@Composable
fun DynamicButton(
    modifier: Modifier = Modifier,
    icon: @Composable () -> Unit,
    icon2: @Composable () -> Unit,
    isIcon1: Boolean,
    onClick: () -> Unit
) {
    IconButton(modifier = modifier, onClick = onClick) {
        if (isIcon1) {
            icon()
        } else {
            icon2()
        }
    }
}