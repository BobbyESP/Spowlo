package com.bobbyesp.spowlo.ui.components.alertDialogs

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.NotInterested
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bobbyesp.spowlo.R
import com.bobbyesp.spowlo.ui.components.dividers.HorizontalDivider
import com.bobbyesp.spowlo.ui.components.text.DotWithText
import com.bobbyesp.spowlo.ui.theme.SpowloTheme


@Composable
fun PermissionNotGrantedDialog(
    modifier: Modifier = Modifier,
    neededPermissions: List<PermissionType>,
    shouldShowRationale: Boolean = false,
    onGrantRequest: () -> Unit,
    onDismissRequest: () -> Unit,
) {
    AlertDialog(
        icon = {
            Icon(
                imageVector = Icons.Outlined.NotInterested,
                contentDescription = "Permission not granted"
            )
        },
        modifier = modifier,
        onDismissRequest = onDismissRequest,
        title = {
            Text(text = stringResource(id = R.string.permission_not_granted))
        },
        text = {
            Column {
                if (shouldShowRationale) {
                    Text(
                        text = stringResource(id = R.string.permission_not_granted_rationale_desc),
                        textAlign = TextAlign.Justify
                    )
                } else {
                    Text(
                        text = stringResource(id = R.string.permission_not_granted_description),
                        textAlign = TextAlign.Justify
                    )
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 6.dp))
                Text(text = stringResource(id = R.string.permissions_to_grant))

                Column(
                    modifier = Modifier.padding(6.dp),
                    verticalArrangement = Arrangement.spacedBy(2.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    neededPermissions.forEach {
                        DotWithText(text = it.toPermissionString())
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = onGrantRequest
            ) {
                Text(stringResource(id = R.string.grant))
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismissRequest
            ) {
                Text(stringResource(id = R.string.dismiss))
            }
        }
    )
}

enum class PermissionType {
    READ_EXTERNAL_STORAGE,
    WRITE_EXTERNAL_STORAGE,
    READ_MEDIA_AUDIO,
    INTERNET,
    ACCESS_NETWORK_STATE,
    ACCESS_WIFI_STATE,
    CHANGE_WIFI_STATE,
    MANAGE_EXTERNAL_STORAGE;

    @SuppressLint("InlinedApi")
    fun getPermissionString(): String {
        return when (this) {
            READ_EXTERNAL_STORAGE -> android.Manifest.permission.READ_EXTERNAL_STORAGE
            WRITE_EXTERNAL_STORAGE -> android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            INTERNET -> android.Manifest.permission.INTERNET
            ACCESS_NETWORK_STATE -> android.Manifest.permission.ACCESS_NETWORK_STATE
            ACCESS_WIFI_STATE -> android.Manifest.permission.ACCESS_WIFI_STATE
            CHANGE_WIFI_STATE -> android.Manifest.permission.CHANGE_WIFI_STATE
            READ_MEDIA_AUDIO -> android.Manifest.permission.READ_MEDIA_AUDIO
            MANAGE_EXTERNAL_STORAGE -> android.Manifest.permission.MANAGE_EXTERNAL_STORAGE
        }
    }

    @Composable
    fun toPermissionString(): String {
        return when (this) {
            READ_EXTERNAL_STORAGE -> stringResource(R.string.read_external_storage)
            WRITE_EXTERNAL_STORAGE -> stringResource(R.string.write_external_storage)
            INTERNET -> stringResource(R.string.internet)
            ACCESS_NETWORK_STATE -> stringResource(R.string.access_network_state)
            ACCESS_WIFI_STATE -> stringResource(R.string.access_wifi_state)
            CHANGE_WIFI_STATE -> stringResource(R.string.change_wifi_state)
            READ_MEDIA_AUDIO -> stringResource(R.string.read_media_audio)
            MANAGE_EXTERNAL_STORAGE -> stringResource(R.string.manage_external_storage)
        }
    }
}

fun String.toPermissionType(): PermissionType {
    return when (this) {
        android.Manifest.permission.READ_EXTERNAL_STORAGE -> PermissionType.READ_EXTERNAL_STORAGE
        android.Manifest.permission.WRITE_EXTERNAL_STORAGE -> PermissionType.WRITE_EXTERNAL_STORAGE
        android.Manifest.permission.INTERNET -> PermissionType.INTERNET
        android.Manifest.permission.ACCESS_NETWORK_STATE -> PermissionType.ACCESS_NETWORK_STATE
        android.Manifest.permission.ACCESS_WIFI_STATE -> PermissionType.ACCESS_WIFI_STATE
        android.Manifest.permission.CHANGE_WIFI_STATE -> PermissionType.CHANGE_WIFI_STATE
        android.Manifest.permission.READ_MEDIA_AUDIO -> PermissionType.READ_MEDIA_AUDIO
        android.Manifest.permission.MANAGE_EXTERNAL_STORAGE -> PermissionType.MANAGE_EXTERNAL_STORAGE
        else -> throw IllegalArgumentException("Unknown permission string")
    }
}

@Preview(uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Preview
@Composable
fun PermissionNotGrantedPreview() {
    SpowloTheme {
        PermissionNotGrantedDialog(
            onGrantRequest = {},
            onDismissRequest = {},
            neededPermissions = listOf(
                PermissionType.READ_EXTERNAL_STORAGE,
                PermissionType.WRITE_EXTERNAL_STORAGE
            )
        )
    }
}