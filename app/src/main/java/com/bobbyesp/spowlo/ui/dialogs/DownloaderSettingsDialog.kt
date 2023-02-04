package com.bobbyesp.spowlo.ui.dialogs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material.icons.outlined.DoneAll
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.DownloadDone
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bobbyesp.spowlo.R
import com.bobbyesp.spowlo.ui.common.intState
import com.bobbyesp.spowlo.ui.components.BottomDrawer
import com.bobbyesp.spowlo.ui.components.DismissButton
import com.bobbyesp.spowlo.ui.components.DrawerSheetSubtitle
import com.bobbyesp.spowlo.ui.components.FilledButtonWithIcon
import com.bobbyesp.spowlo.ui.components.OutlinedButtonWithIcon
import com.bobbyesp.spowlo.utils.CUSTOM_COMMAND
import com.bobbyesp.spowlo.utils.PreferencesUtil
import com.bobbyesp.spowlo.utils.PreferencesUtil.templateStateFlow
import com.bobbyesp.spowlo.utils.TEMPLATE_ID
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class, ExperimentalLifecycleComposeApi::class)
@Composable
fun DownloaderSettingsDialog(
    useDialog: Boolean = false,
    dialogState: Boolean = false,
    isShareActivity: Boolean = false,
    drawerState: ModalBottomSheetState,
    confirm: () -> Unit,
    hide: () -> Unit
) {
    val settings = PreferencesUtil

    var customCommand by remember { mutableStateOf(PreferencesUtil.getValue(CUSTOM_COMMAND)) }
    var selectedTemplateId by TEMPLATE_ID.intState

    val showAudioFormatDialog by remember { mutableStateOf(false) }
    var showAudioQualityDialog by remember { mutableStateOf(false) }
    var showCustomCommandDialog by remember { mutableStateOf(0) }

    val templateList by templateStateFlow.collectAsStateWithLifecycle(ArrayList())
    val scrollState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    LaunchedEffect(templateList.size, customCommand) {
        if (customCommand) {
            templateList.indexOfFirst { it.id == selectedTemplateId }
                .run { if (!equals(-1)) scrollState.scrollToItem(this) }
        }
    }

    val updatePreferences = {
        scope.launch {
            settings.updateValue(CUSTOM_COMMAND, customCommand)
            settings.encodeInt(TEMPLATE_ID, selectedTemplateId)
        }
    }

    val downloadButtonCallback = {
        updatePreferences()
        hide()
        confirm()
    }

    val sheetContent: @Composable () -> Unit = {
        Column() {
            Text(
                text = stringResource(R.string.settings_before_download_text),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            DrawerSheetSubtitle(text = stringResource(id = R.string.general_settings))
        }
    }
    if (!useDialog) {
        BottomDrawer(drawerState = drawerState, sheetContent = {
            Icon(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                imageVector = Icons.Outlined.DownloadDone,
                contentDescription = null
            )
            Text(
                text = stringResource(R.string.settings_before_download),
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(vertical = 16.dp),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis, textAlign = TextAlign.Center
            )
            sheetContent()
            val state = rememberLazyListState()
            LaunchedEffect(drawerState.isVisible) {
                state.scrollToItem(1)
            }
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp),
                horizontalArrangement = Arrangement.End,
                state = state
            ) {
                item {
                    OutlinedButtonWithIcon(
                        modifier = Modifier.padding(horizontal = 12.dp),
                        onClick = hide,
                        icon = Icons.Outlined.Cancel,
                        text = stringResource(R.string.cancel)
                    )
                }
                item {
                    FilledButtonWithIcon(
                        onClick = downloadButtonCallback,
                        icon = Icons.Outlined.DownloadDone,
                        text = stringResource(R.string.start_download)
                    )
                }
            }
        })
    } else if (dialogState) {
        AlertDialog(
            onDismissRequest = hide,
            confirmButton = {
                TextButton(onClick = downloadButtonCallback) {
                    Text(text = stringResource(R.string.start_download))
                }
            },
            dismissButton = { DismissButton { hide() } },
            icon = {
                Icon(
                    imageVector = Icons.Outlined.DoneAll,
                    contentDescription = null
                )
            },
            title = {
                Text(
                    stringResource(R.string.settings_before_download),
                    textAlign = TextAlign.Center
                )
            },
            text = {
                Column(Modifier.verticalScroll(rememberScrollState())) {
                    sheetContent()
                }
            })
    }

}