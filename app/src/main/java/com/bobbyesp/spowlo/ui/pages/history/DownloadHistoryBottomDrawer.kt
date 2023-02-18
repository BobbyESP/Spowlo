package com.bobbyesp.spowlo.ui.pages.history

import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bobbyesp.spowlo.R
import com.bobbyesp.spowlo.ui.common.AsyncImageImpl
import com.bobbyesp.spowlo.ui.components.BottomDrawer
import com.bobbyesp.spowlo.ui.components.FilledTonalButtonWithIcon
import com.bobbyesp.spowlo.ui.components.MarqueeText
import com.bobbyesp.spowlo.ui.components.OpenInSpotifyFilledButton
import com.bobbyesp.spowlo.ui.components.OutlinedButtonWithIcon
import com.bobbyesp.spowlo.utils.FilesUtil

@OptIn(ExperimentalLifecycleComposeApi::class, ExperimentalMaterialApi::class)
@Composable
fun DownloadHistoryBottomDrawer(downloadsHistoryViewModel: DownloadsHistoryViewModel = hiltViewModel()) {

    val viewState = downloadsHistoryViewModel.detailViewState.collectAsStateWithLifecycle().value
    val scope = rememberCoroutineScope()
    val uriHandler = LocalUriHandler.current
    val context = LocalContext.current
    val hapticFeedback = LocalHapticFeedback.current

    val shareStringResource = stringResource(id = R.string.share)

    BackHandler(viewState.drawerState.targetValue == ModalBottomSheetValue.Expanded) {
        downloadsHistoryViewModel.hideDrawer(scope)
    }

    with(viewState) {
        DownloadHistoryBottomDrawerImpl(
            drawerState = drawerState,
            songName = title,
            songAuthor = author,
            songUrl = url,
            artworkUrl = artworkUrl,
            onDelete = {
                downloadsHistoryViewModel.hideDrawer(scope)
                downloadsHistoryViewModel.showDialog()
            },
            onShareFile = {
                FilesUtil.createIntentForSharingFile(path)?.runCatching {
                    context.startActivity(
                        Intent.createChooser(this, shareStringResource)
                    )
                }
            },
        onOpenLink = {
            hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
            downloadsHistoryViewModel.hideDrawer(scope)
            uriHandler.openUri(url)
        })
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun DownloadHistoryBottomDrawerImpl(
    drawerState: ModalBottomSheetState = ModalBottomSheetState(ModalBottomSheetValue.Hidden),
    songName: String,
    songAuthor: String,
    songUrl: String,
    artworkUrl: String,
    onDelete: () -> Unit,
    onOpenLink: () -> Unit,
    onShareFile: () -> Unit
) {
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current
    BottomDrawer(drawerState = drawerState, sheetContent = {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImageImpl(
                    modifier = Modifier
                        .size(64.dp)
                        .aspectRatio(
                            1f,
                            matchHeightConstraintsFirst = true
                        )
                        .clip(MaterialTheme.shapes.small),
                    model = artworkUrl,
                    contentDescription = stringResource(id = R.string.song_cover),
                    contentScale = ContentScale.Crop,
                )
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 12.dp)
                        .weight(1f)
                ) {
                    SelectionContainer {
                        MarqueeText(
                            text = songName,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleLarge
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    SelectionContainer {
                        Text(
                            text = songAuthor,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.alpha(alpha = 0.8f)
                        )
                    }
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(top = 24.dp), horizontalArrangement = Arrangement.End
            ) {
                OutlinedButtonWithIcon(
                    modifier = Modifier.padding(horizontal = 12.dp),
                    onClick = onDelete,
                    icon = Icons.Outlined.Delete,
                    text = stringResource(R.string.remove)
                )
                FilledTonalButtonWithIcon(
                    onClick = onShareFile,
                    icon = Icons.Outlined.Share,
                    text = stringResource(R.string.share_file)
                )
                OpenInSpotifyFilledButton(modifier = Modifier.padding(start = 12.dp), onClick = onOpenLink)
            }
        }

    })
}