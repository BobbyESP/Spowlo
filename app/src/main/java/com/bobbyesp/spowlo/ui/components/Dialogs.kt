package com.bobbyesp.spowlo.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.HelpOutline
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.google.accompanist.flowlayout.FlowRow
import com.bobbyesp.spowlo.R

private val DialogVerticlePadding = PaddingValues(vertical = 24.dp)
private val IconPadding = PaddingValues(bottom = 16.dp)
private val DialogHorizontalPadding = PaddingValues(horizontal = 24.dp)
private val TitlePadding = PaddingValues(bottom = 16.dp)
private val TextPadding = PaddingValues(bottom = 24.dp)
private val ButtonsMainAxisSpacing = 8.dp
private val ButtonsCrossAxisSpacing = 12.dp

@Composable
fun HelpDialog(text: String, onDismissRequest: () -> Unit = {}) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(text = stringResource(id = R.string.how_does_it_work)) },
        icon = { Icon(Icons.Outlined.HelpOutline, null) },
        text = { Text(text = text) },
        confirmButton = { ConfirmButton { onDismissRequest() } },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpowloDialog(
    onDismissRequest: () -> Unit,
    confirmButton: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    dismissButton: @Composable (() -> Unit)? = null,
    icon: @Composable (() -> Unit)? = null,
    title: @Composable (() -> Unit)? = null,
    text: @Composable (() -> Unit)? = null,
    shape: Shape = AlertDialogDefaults.shape,
    containerColor: Color = AlertDialogDefaults.containerColor,
    iconContentColor: Color = AlertDialogDefaults.iconContentColor,
    titleContentColor: Color = AlertDialogDefaults.titleContentColor,
    textContentColor: Color = AlertDialogDefaults.textContentColor,
    tonalElevation: Dp = AlertDialogDefaults.TonalElevation,
    properties: DialogProperties = DialogProperties()
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        modifier = modifier,
        properties = properties,
        icon = {
            CompositionLocalProvider(LocalContentColor provides iconContentColor) {
                Box(
                    Modifier
                        .padding(IconPadding)
                        .padding(DialogHorizontalPadding)
                ) {
                    if (icon != null) {
                        icon()
                    }
                }
            }
        },
        title = {
            CompositionLocalProvider(LocalContentColor provides titleContentColor) {
                val textStyle = MaterialTheme.typography.headlineSmall
                ProvideTextStyle(textStyle) {
                    Box(
                        // Align the title to the center when an icon is present.
                        Modifier
                            .padding(TitlePadding)
                            .padding(DialogHorizontalPadding)
                    ) {
                        if (title != null) {
                            title()
                        }
                    }
                }
            }
        },
        text = {
            CompositionLocalProvider(LocalContentColor provides textContentColor) {
                val textStyle =
                    MaterialTheme.typography.bodyMedium
                ProvideTextStyle(textStyle) {
                    Box(
                    ) {
                        if (text != null) {
                            text()
                        }
                    }
                }
            }
        },
        confirmButton = confirmButton,
        dismissButton = dismissButton,
        shape = shape,
        containerColor = containerColor,
        tonalElevation = tonalElevation
    )
}