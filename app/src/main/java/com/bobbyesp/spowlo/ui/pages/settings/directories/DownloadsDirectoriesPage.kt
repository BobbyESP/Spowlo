package com.bobbyesp.spowlo.ui.pages.settings.directories

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SdCardAlert
import androidx.compose.material.icons.outlined.LibraryMusic
import androidx.compose.material.icons.outlined.SdCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import com.bobbyesp.spowlo.utils.CUSTOM_PATH
import com.bobbyesp.spowlo.utils.PreferencesUtil
import com.bobbyesp.spowlo.utils.SUBDIRECTORY
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.bobbyesp.spowlo.App
import com.bobbyesp.spowlo.R
import com.bobbyesp.spowlo.ui.common.LocalDarkTheme
import com.bobbyesp.spowlo.ui.components.BackButton
import com.bobbyesp.spowlo.ui.components.LargeTopAppBar
import com.bobbyesp.spowlo.ui.components.PreferenceInfo
import com.bobbyesp.spowlo.ui.components.PreferenceItem
import com.bobbyesp.spowlo.ui.components.PreferenceSubtitle
import com.bobbyesp.spowlo.ui.components.PreferenceSwitchWithDivider
import com.bobbyesp.spowlo.ui.components.PreferencesHintCard
import com.bobbyesp.spowlo.utils.CUSTOM_COMMAND
import com.bobbyesp.spowlo.utils.FilesUtil
import com.bobbyesp.spowlo.utils.PreferencesUtil.getString
import com.bobbyesp.spowlo.utils.SDCARD_DOWNLOAD
import com.bobbyesp.spowlo.utils.SDCARD_URI
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberPermissionState

private const val validDirectoryRegex = "/storage/emulated/0/(Download|Documents)"
private fun String.isValidDirectory(): Boolean {
    return this.contains(Regex(validDirectoryRegex))
}
private enum class Directory {
    AUDIO, SDCARD
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun DownloadsDirectoriesPage(
    onBackPressed: () -> Unit,
) {

    val uriHandler = LocalUriHandler.current
    val clipboardManager = LocalClipboardManager.current
    val scope = rememberCoroutineScope()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        rememberTopAppBarState(),
        canScroll = { true }
    )
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    var isSubdirectoryEnabled
            by remember { mutableStateOf(PreferencesUtil.getValue(SUBDIRECTORY)) }
    var isCustomPathEnabled
            by remember { mutableStateOf(PreferencesUtil.getValue(CUSTOM_PATH)) }

    var audioDirectoryText by remember {
        mutableStateOf(App.audioDownloadDir)
    }

    var sdcardUri by remember {
        mutableStateOf(SDCARD_URI.getString())
    }
    var sdcardDownload by remember {
        mutableStateOf(PreferencesUtil.getValue(SDCARD_DOWNLOAD))
    }

    var pathTemplateText by remember { mutableStateOf(PreferencesUtil.getOutputPathTemplate()) }

    var showClearTempDialog by remember { mutableStateOf(false) }

    var editingDirectory by remember { mutableStateOf(Directory.AUDIO) }

    val isCustomCommandEnabled by remember {
        mutableStateOf(PreferencesUtil.getValue(CUSTOM_COMMAND))
    }

    val storagePermission =
        rememberPermissionState(permission = Manifest.permission.WRITE_EXTERNAL_STORAGE)

    val showDirectoryAlert =
        Build.VERSION.SDK_INT >= 30 && !Environment.isExternalStorageManager()
                && (!audioDirectoryText.isValidDirectory())

    val launcher =
        rememberLauncherForActivityResult(object : ActivityResultContracts.OpenDocumentTree() {
            override fun createIntent(context: Context, input: Uri?): Intent {
                return (super.createIntent(context, input)).apply {
                    flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or
                            Intent.FLAG_GRANT_WRITE_URI_PERMISSION or
                            Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION
                }
            }
        }) {
            it?.let {
                if (editingDirectory == Directory.SDCARD) {
                    sdcardUri = it.toString()
                    PreferencesUtil.encodeString(SDCARD_URI, it.toString())
                    context.contentResolver?.takePersistableUriPermission(
                        it,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION or
                                Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                    )
                    return@let
                }
                val path = FilesUtil.getRealPath(it)
                App.updateDownloadDir(path)
                    audioDirectoryText = path
            }
        }

    fun openDirectoryChooser() {
        if (Build.VERSION.SDK_INT > 29 || storagePermission.status == PermissionStatus.Granted)
            launcher.launch(null)
        else storagePermission.launchPermissionRequest()
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        snackbarHost = {
            SnackbarHost(
                modifier = Modifier.systemBarsPadding(),
                hostState = snackbarHostState
            )
        },
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(
                        modifier = Modifier,
                        text = stringResource(id = R.string.download_directory),
                    )
                }, navigationIcon = {
                    BackButton {
                        onBackPressed()
                    }
                }, scrollBehavior = scrollBehavior
            )
        }, content = {
            LazyColumn(modifier = Modifier.padding(it)) {
                if(sdcardUri.isEmpty())
                    item {
                        PreferenceInfo(text = stringResource(id = R.string.sdcard_not_activable_hint))
                    }
                if (showDirectoryAlert)
                    item {
                        PreferencesHintCard(
                            title = stringResource(R.string.permission_issue),
                            description = stringResource(R.string.permission_issue_desc),
                            icon = Icons.Filled.SdCardAlert,
                            isDarkTheme = LocalDarkTheme.current.isDarkTheme()
                        ) {
                            if (Build.VERSION.SDK_INT >= 30 && !Environment.isExternalStorageManager()) {
                                Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION).apply {
                                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                    data = Uri.parse("package:" + context.packageName)
                                    if (resolveActivity(context.packageManager) != null)
                                        context.startActivity(this)
                                }
                            }
                        }
                    }
                item {
                    Text(
                        text = stringResource(id = R.string.general_settings),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 18.dp, top = 12.dp, bottom = 4.dp),
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.labelLarge
                    )
                }
                item{
                    PreferenceItem(
                        title = stringResource(id = R.string.audio_directory),
                        description = audioDirectoryText,
                        enabled = !isCustomCommandEnabled && !sdcardDownload,
                        icon = Icons.Outlined.LibraryMusic
                    ) {
                        editingDirectory = Directory.AUDIO
                        openDirectoryChooser()
                    }
                }
                item {
                    PreferenceSwitchWithDivider(
                        title = stringResource(id = R.string.sdcard_directory),
                        description = sdcardUri,
                        isChecked = sdcardDownload,
                        enabled = !isCustomCommandEnabled,
                        isSwitchEnabled = !isCustomCommandEnabled && sdcardUri.isNotBlank(),
                        onChecked = {
                            sdcardDownload = !sdcardDownload
                            PreferencesUtil.updateValue(SDCARD_DOWNLOAD, sdcardDownload)
                        },
                        icon = Icons.Outlined.SdCard,
                        onClick = {
                            editingDirectory = Directory.SDCARD
                            openDirectoryChooser()
                        }
                    )
                }
            }
        })

}