package com.bobbyesp.spowlo.ui.pages.settings.cookies

import android.webkit.CookieManager
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.ContentPasteGo
import androidx.compose.material.icons.outlined.Cookie
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.DeleteForever
import androidx.compose.material.icons.outlined.GeneratingTokens
import androidx.compose.material.icons.outlined.HelpOutline
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.LineBreak
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bobbyesp.spowlo.R
import com.bobbyesp.spowlo.ui.components.BackButton
import com.bobbyesp.spowlo.ui.components.ConfirmButton
import com.bobbyesp.spowlo.ui.components.DismissButton
import com.bobbyesp.spowlo.ui.components.HelpDialog
import com.bobbyesp.spowlo.ui.components.HorizontalDivider
import com.bobbyesp.spowlo.ui.components.PasteFromClipBoardButton
import com.bobbyesp.spowlo.ui.components.PreferenceItemVariant
import com.bobbyesp.spowlo.ui.components.PreferenceSwitchWithContainer
import com.bobbyesp.spowlo.ui.components.TextButtonWithIcon
import com.bobbyesp.spowlo.utils.COOKIES
import com.bobbyesp.spowlo.utils.DownloaderUtil
import com.bobbyesp.spowlo.utils.FilesUtil
import com.bobbyesp.spowlo.utils.FilesUtil.getCookiesFile
import com.bobbyesp.spowlo.utils.PreferencesUtil
import com.bobbyesp.spowlo.utils.matchUrlFromClipboard
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CookieProfilePage(
    cookiesViewModel: CookiesSettingsViewModel = viewModel(),
    navigateToCookieGeneratorPage: () -> Unit = {},
    onBackPressed: () -> Unit = {}
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        rememberTopAppBarState(),
        canScroll = { true })
    val cookies = cookiesViewModel.cookiesFlow.collectAsState(emptyList()).value
    val scope = rememberCoroutineScope()
    val hapticFeedback = LocalHapticFeedback.current
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val state by cookiesViewModel.stateFlow.collectAsStateWithLifecycle()
    var showClearCookieDialog by remember { mutableStateOf(false) }
    var isCookieEnabled by remember { mutableStateOf(PreferencesUtil.getValue(COOKIES)) }
    val cookieManager = CookieManager.getInstance()
    var showHelpDialog by remember { mutableStateOf(false) }

    LaunchedEffect(state.showEditDialog) {
        withContext(Dispatchers.IO) {
            DownloaderUtil.getCookiesContentFromDatabase().getOrNull()?.let {
                FilesUtil.writeContentToFile(it, context.getCookiesFile())
            }
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(title = {
                Text(
                    modifier = Modifier,
                    text = stringResource(id = R.string.cookies),
                )
            }, navigationIcon = {
                BackButton {
                    onBackPressed()
                }
            }, actions = {
                IconButton(onClick = { showHelpDialog = true }) {
                    Icon(
                        imageVector = Icons.Outlined.HelpOutline,
                        contentDescription = stringResource(R.string.how_does_it_work)
                    )
                }
            }, scrollBehavior = scrollBehavior)
        },
    )
    { paddingValues ->
        LazyColumn(modifier = Modifier.padding(paddingValues)) {
            item {
                PreferenceSwitchWithContainer(
                    title = stringResource(R.string.use_cookies),
                    icon = null,
                    isChecked = isCookieEnabled,
                    onClick = {
                        if (cookies.isEmpty() || (!cookieManager.hasCookies()) && !isCookieEnabled)
                            showHelpDialog = true
                        else {
                            isCookieEnabled = !isCookieEnabled
                            PreferencesUtil.updateValue(COOKIES, isCookieEnabled)
                        }
                    })
            }
            itemsIndexed(cookies) { _, item ->
                PreferenceItemVariant(
                    modifier = Modifier.padding(vertical = 4.dp),
                    title = item.url,
                    onClick = { cookiesViewModel.showEditCookieDialog(item) },
                    onClickLabel = stringResource(
                        id = R.string.edit
                    ), onLongClick = {
                        hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                        cookiesViewModel.showDeleteCookieDialog(item)
                    }, onLongClickLabel = stringResource(R.string.remove)
                )
            }

            item {
                HorizontalDivider()
                PreferenceItemVariant(
                    title = stringResource(id = R.string.generate_new_cookies),
                    icon = Icons.Outlined.Add
                ) { cookiesViewModel.showEditCookieDialog() }
            }
            item {
                PreferenceItemVariant(
                    title = stringResource(id = R.string.export_to_clipboard),
                    icon = Icons.Outlined.ContentPasteGo
                ) {
                    scope.launch(Dispatchers.IO) {
                        DownloaderUtil.getCookiesContentFromDatabase().getOrNull()?.let {
                            clipboardManager.setText(AnnotatedString(it))
                        }
                    }
                }
            }
            item {
                PreferenceItemVariant(
                    title = stringResource(id = R.string.clear_all_cookies),
                    icon = Icons.Outlined.DeleteForever
                ) {
                    hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                    showClearCookieDialog = true
                }
            }
        }

    }
    if (state.showEditDialog) {
        CookieGeneratorDialog(
            cookiesViewModel,
            navigateToCookieGeneratorPage = navigateToCookieGeneratorPage
        ) {
            cookiesViewModel.hideDialog()
        }
    }

    if (state.showDeleteDialog) {
        DeleteCookieDialog(cookiesViewModel) { cookiesViewModel.hideDialog() }
    }

    if (showHelpDialog) {
        HelpDialog(text = stringResource(id = R.string.cookies_usage_msg)) {
            showHelpDialog = false
        }
    }
    if (showClearCookieDialog) {
        ClearCookiesDialog(onDismissRequest = { showClearCookieDialog = false }) {
            scope.launch(Dispatchers.IO) {
                CookieManager.getInstance().removeAllCookies(null)
            }
        }
    }
}

@OptIn(
    ExperimentalMaterial3Api::class
)
@Composable
fun CookieGeneratorDialog(
    cookiesViewModel: CookiesSettingsViewModel = viewModel(),
    navigateToCookieGeneratorPage: () -> Unit = {},
    onDismissRequest: () -> Unit
) {

    val state by cookiesViewModel.stateFlow.collectAsStateWithLifecycle()
    val profile = state.editingCookieProfile
    val url = profile.url

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            CookieManager.getInstance().flush()
        }
    }
    AlertDialog(onDismissRequest = onDismissRequest, icon = {
        Icon(Icons.Outlined.Cookie, null)
    }, title = { Text(stringResource(R.string.cookies)) }, text = {
        Column(Modifier.verticalScroll(rememberScrollState())) {
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth(),
                value = url, label = { Text("URL") },
                onValueChange = { cookiesViewModel.updateUrl(it) }, trailingIcon = {
                    PasteFromClipBoardButton {
                        cookiesViewModel.updateUrl(
                            matchUrlFromClipboard(it)
                        )
                    }
                }, maxLines = 1,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)
            )

            TextButtonWithIcon(
                onClick = { navigateToCookieGeneratorPage() },
                icon = Icons.Outlined.GeneratingTokens,
                text = stringResource(id = R.string.generate_new_cookies)
            )

        }
    }, dismissButton = {
        DismissButton {
            onDismissRequest()
        }
    }, confirmButton = {
        ConfirmButton(enabled = url.isNotEmpty()) {
            cookiesViewModel.updateCookieProfile()
            onDismissRequest()
        }
    })

}

@OptIn(ExperimentalTextApi::class)
@Composable
fun DeleteCookieDialog(
    cookiesViewModel: CookiesSettingsViewModel = viewModel(),
    onDismissRequest: () -> Unit = {}
) {
    val state by cookiesViewModel.stateFlow.collectAsState()
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(stringResource(R.string.remove)) },
        text = {
            Text(
                stringResource(R.string.remove_cookie_profile_desc).format(state.editingCookieProfile.url),
                style = LocalTextStyle.current.copy(lineBreak = LineBreak.Paragraph)
            )
        },
        dismissButton = {
            DismissButton {
                onDismissRequest()
            }
        }, confirmButton = {
            ConfirmButton {
                cookiesViewModel.deleteCookieProfile()
                onDismissRequest()
            }
        }, icon = { Icon(Icons.Outlined.Delete, null) })
}

@Composable
fun ClearCookiesDialog(
    onDismissRequest: () -> Unit = {}, onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(stringResource(R.string.clear_all_cookies)) },
        text = {
            Text(
                stringResource(R.string.clear_all_cookies_desc),
                style = MaterialTheme.typography.bodyLarge
            )
        },
        dismissButton = {
            DismissButton {
                onDismissRequest()
            }
        }, confirmButton = {
            ConfirmButton {
                onConfirm()
                onDismissRequest()
            }
        }, icon = { Icon(Icons.Outlined.DeleteForever, null) })
}