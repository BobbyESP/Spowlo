package com.bobbyesp.spowlo.ui.pages.downloader

import android.Manifest
import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ContentAlpha
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ContentPaste
import androidx.compose.material.icons.outlined.Error
import androidx.compose.material.icons.outlined.FileDownload
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Subscriptions
import androidx.compose.material.icons.outlined.Terminal
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberPermissionState
import com.bobbyesp.spowlo.App
import com.bobbyesp.spowlo.Downloader
import com.bobbyesp.spowlo.R
import com.bobbyesp.spowlo.ui.common.LocalWindowWidthState
import com.bobbyesp.spowlo.ui.components.ClearButton
import com.bobbyesp.spowlo.ui.components.NavigationBarSpacer
import com.bobbyesp.spowlo.ui.components.songs.SongCard
import com.bobbyesp.spowlo.ui.dialogs.DownloaderSettingsDialog
import com.bobbyesp.spowlo.utils.CONFIGURE
import com.bobbyesp.spowlo.utils.CUSTOM_COMMAND
import com.bobbyesp.spowlo.utils.DEBUG
import com.bobbyesp.spowlo.utils.PreferencesUtil
import com.bobbyesp.spowlo.utils.PreferencesUtil.getBoolean
import com.bobbyesp.spowlo.utils.ToastUtil
import com.bobbyesp.spowlo.utils.matchUrlFromClipboard

@Composable
@OptIn(
    ExperimentalAnimationApi::class, ExperimentalMaterial3Api::class,
    ExperimentalPermissionsApi::class, ExperimentalComposeUiApi::class,
    ExperimentalLifecycleComposeApi::class, ExperimentalMaterialApi::class
)
fun DownloaderPage(
    navigateToSettings: () -> Unit = {},
    navigateToDownloads: () -> Unit = {},
    navigateToPlaylistPage: () -> Unit = {},
    onNavigateToTaskList: () -> Unit = {},
    downloaderViewModel: DownloaderViewModel = hiltViewModel(),
) {
    val scope = rememberCoroutineScope()
    val storagePermission = rememberPermissionState(
        permission = Manifest.permission.WRITE_EXTERNAL_STORAGE
    ) { b: Boolean ->
        if (b) {
            downloaderViewModel.startDownloadSong()
        } else {
            ToastUtil.makeToast(R.string.permission_denied)
        }
    }

    //STATE FLOWS
    val viewState by downloaderViewModel.viewStateFlow.collectAsStateWithLifecycle()
    val downloaderState by Downloader.downloaderState.collectAsStateWithLifecycle()
    val taskState by Downloader.taskState.collectAsStateWithLifecycle()
    val errorState by Downloader.errorState.collectAsStateWithLifecycle()

    val useDialog = LocalWindowWidthState.current != WindowWidthSizeClass.Compact

    val clipboardManager = LocalClipboardManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    val checkPermissionOrDownload = {
        if (Build.VERSION.SDK_INT > 29 || storagePermission.status == PermissionStatus.Granted)
        downloaderViewModel.startDownloadSong()
        else {
            storagePermission.launchPermissionRequest()
        }
    }

    val downloadCallback = {
        if (CONFIGURE.getBoolean()) downloaderViewModel.showDialog(
            scope,
            useDialog
        )
        else checkPermissionOrDownload()
        keyboardController?.hide()
    }

    var showConsoleOutput by remember { mutableStateOf(DEBUG.getBoolean()) }

    LaunchedEffect(downloaderState) {
        showConsoleOutput =
            PreferencesUtil.getValue(DEBUG) && downloaderState !is Downloader.State.Idle
    }

    if (viewState.isUrlSharingTriggered) {
        downloadCallback()
        downloaderViewModel.onShareIntentConsumed()
    }

    BackHandler(viewState.drawerState.isVisible) {
        downloaderViewModel.hideDialog(scope, useDialog)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        DownloaderPageImplementation(
            downloaderState = downloaderState,
            taskState = taskState,
            viewState = viewState,
            errorState = errorState,
            downloadCallback = { downloadCallback() },
            navigateToSettings = navigateToSettings,
            navigateToDownloads = navigateToDownloads,
            onNavigateToTaskList = onNavigateToTaskList,
            showOutput = showConsoleOutput,
            showSongCard = true,
            showDownloadProgress = taskState.taskId.isNotEmpty(),
            pasteCallback = {
                matchUrlFromClipboard(
                    string = clipboardManager.getText().toString(),
                    isMatchingMultiLink = CUSTOM_COMMAND.getBoolean()
                )
                    .let { downloaderViewModel.updateUrl(it) }
            },
            cancelCallback = {
                Downloader.cancelDownload()
            },
            onUrlChanged = { url -> downloaderViewModel.updateUrl(url) }) {}

        with(viewState) {
            DownloaderSettingsDialog(useDialog = useDialog,
                dialogState = showDownloadSettingDialog,
                drawerState = drawerState,
                confirm = { checkPermissionOrDownload() }) {
                downloaderViewModel.hideDialog(scope, useDialog)
            }
        }
    }
}

@OptIn(
    ExperimentalMaterial3Api::class
)
@Composable
fun DownloaderPageImplementation(
    downloaderState: Downloader.State,
    taskState: Downloader.DownloadTaskItem,
    viewState: DownloaderViewModel.ViewState,
    errorState: Downloader.ErrorState,
    showSongCard: Boolean = false,
    showOutput: Boolean = false,
    showDownloadProgress: Boolean = false,
    downloadCallback: () -> Unit = {},
    navigateToSettings: () -> Unit = {},
    navigateToDownloads: () -> Unit = {},
    onNavigateToTaskList: () -> Unit = {},
    pasteCallback: () -> Unit = {},
    cancelCallback: () -> Unit = {},
    onSongCardClicked: () -> Unit = {},
    onUrlChanged: (String) -> Unit = {},
    isPreview: Boolean = false,
    content: @Composable () -> Unit
) {
    Scaffold(modifier = Modifier.fillMaxSize(), topBar = {
        TopAppBar(title = {}, modifier = Modifier.padding(horizontal = 8.dp), navigationIcon = {
            IconButton(onClick = { navigateToSettings() }) {
                Icon(
                    imageVector = Icons.Outlined.Settings,
                    contentDescription = stringResource(id = R.string.settings)
                )
            }
        }, actions = {

            IconButton(onClick = { onNavigateToTaskList() }) {
                Icon(
                    imageVector = Icons.Outlined.Terminal,
                    contentDescription = stringResource(id = R.string.running_tasks)
                )
            }

            IconButton(onClick = { navigateToDownloads() }) {
                Icon(
                    imageVector = Icons.Outlined.Subscriptions,
                    contentDescription = stringResource(id = R.string.downloads_history)
                )
            }
        })
    }, floatingActionButton = {
        FABs(
            modifier = with(receiver = Modifier) { if (showDownloadProgress) this else this.imePadding() },
            downloadCallback = downloadCallback,
            pasteCallback = pasteCallback
        )
    }) {
        Column(
            modifier = Modifier
                .padding(it)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Column(
                Modifier
                    .padding(horizontal = 24.dp)
                    .padding(top = 24.dp)
            ) {
                Row(
                    modifier = Modifier
                        .padding(top = 12.dp, bottom = 3.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            modifier = Modifier,
                            text = stringResource(R.string.app_name),
                            style = MaterialTheme.typography.displaySmall
                        )
                        Text(
                            modifier = Modifier.alpha(ContentAlpha.medium).padding(top = 2.dp),
                            text = stringResource(R.string.app_description),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    AnimatedVisibility(visible = downloaderState !is Downloader.State.Idle) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 3.dp,
                        )
                    }
                }
                with(taskState) {
                    AnimatedVisibility(visible = showSongCard && showDownloadProgress) {
                        SongCard(
                            song = info[0],
                            progress = progress,
                            modifier = Modifier.padding(top = 16.dp, bottom = 16.dp),
                            isLyrics = info[0].lyrics?.isNotEmpty()
                                ?: false,
                            isExplicit = info[0].explicit,
                            onClick = { onSongCardClicked() }
                        )
                    }
                    InputUrl(
                        url = viewState.url,
                        progress = progress,
                        showDownloadProgress = showDownloadProgress && !showSongCard,
                        error = errorState.isErrorOccurred(),
                        onDone = downloadCallback,
                    ) { url -> onUrlChanged(url) }
                    AnimatedVisibility(
                        enter = expandVertically() + fadeIn(),
                        exit = shrinkVertically() + fadeOut(),
                        visible = progressText.isNotEmpty() && showOutput
                    ) {
                        ElevatedCard(modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp)) {
                            Column {
                                Text(
                                    text = "Log",
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(16.dp)
                                )
                                Text(
                                    modifier = Modifier.padding(start = 8.dp,bottom = 12.dp),
                                    text = progressText,
                                    maxLines = 3,
                                    overflow = TextOverflow.Ellipsis,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }

                        }

                    }
                    AnimatedVisibility(visible = errorState.isErrorOccurred()) {
                        ErrorMessage(
                            url = viewState.url,
                            errorMessageResId = errorState.errorMessageResId,
                            errorReport = errorState.errorReport
                        )
                    }
                    content()
                    NavigationBarSpacer()
                    Spacer(modifier = Modifier.height(160.dp))
                }
            }
        }
    }
}

@Composable
fun FABs(
    modifier: Modifier = Modifier,
    downloadCallback: () -> Unit = {},
    pasteCallback: () -> Unit = {},
) {
    Column(
        modifier = modifier.padding(6.dp), horizontalAlignment = Alignment.End
    ) {
        FloatingActionButton(
            onClick = pasteCallback,
            content = {
                Icon(
                    Icons.Outlined.ContentPaste, contentDescription = stringResource(R.string.paste)
                )
            },
            modifier = Modifier.padding(vertical = 12.dp),
        )
        FloatingActionButton(
            onClick = downloadCallback, content = {
                Icon(
                    Icons.Outlined.FileDownload,
                    contentDescription = stringResource(R.string.download)
                )
            }, modifier = Modifier.padding(vertical = 12.dp)
        )
    }

}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun InputUrl(
    url: String,
    error: Boolean,
    showDownloadProgress: Boolean = false,
    progress: Float,
    onDone: () -> Unit,
    onValueChange: (String) -> Unit
) {
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    val softwareKeyboardController = LocalSoftwareKeyboardController.current
    OutlinedTextField(
        value = url,
        isError = error,
        onValueChange = onValueChange,
        label = { Text(stringResource(R.string.url_query_spotify)) },
        modifier = Modifier
            .padding(0f.dp, 16f.dp)
            .fillMaxWidth()
            .focusRequester(focusRequester),
        textStyle = MaterialTheme.typography.bodyLarge,
        maxLines = 3,
        trailingIcon = {
            if (url.isNotEmpty()) ClearButton { onValueChange("") }
//            else PasteUrlButton { onPaste() }
        }, keyboardActions = KeyboardActions(onDone = {
            softwareKeyboardController?.hide()
            focusManager.moveFocus(FocusDirection.Down)
            onDone()
        }),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)
    )
    AnimatedVisibility(visible = showDownloadProgress) {
        Row(
            Modifier.padding(0.dp, 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            val progressAnimationValue by animateFloatAsState(
                targetValue = progress / 100f,
                animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing)
            )
            if (progressAnimationValue < 0) LinearProgressIndicator(
                modifier = Modifier
                    .weight(0.75f)
                    .clip(MaterialTheme.shapes.large),
            )
            else LinearProgressIndicator(
                progress = progressAnimationValue,
                modifier = Modifier
                    .weight(0.75f)
                    .clip(MaterialTheme.shapes.large),
            )
            Text(
                text = if (progress < 0) "0%" else "$progress%",
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(0.25f)
            )
        }
    }
}

@Composable
fun ErrorMessage(
    modifier: Modifier = Modifier,
    url: String,
    errorReport: String = "",
    errorMessageResId: Int,
) {
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current
    Row(modifier = modifier
        .fillMaxWidth()
        .run {
            if (errorReport.isNotEmpty()) {
                clip(MaterialTheme.shapes.large).clickable {
                    clipboardManager.setText(AnnotatedString(App.getVersionReport() + "\nURL: $url\n$errorReport"))
                    ToastUtil.makeToastSuspend(context.getString(R.string.error_copied))
                }
            } else this
        }
        .padding(horizontal = 8.dp, vertical = 8.dp)) {
        Icon(
            Icons.Outlined.Error, contentDescription = null, tint = MaterialTheme.colorScheme.error
        )
        Text(
            maxLines = 10,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(start = 6.dp),
            text = errorReport.ifEmpty { stringResource(id = errorMessageResId) },
            color = MaterialTheme.colorScheme.error
        )
    }
}