package com.bobbyesp.spowlo.ui.pages.history

import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
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
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Link
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
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
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bobbyesp.spowlo.R
import com.bobbyesp.spowlo.ui.common.AsyncImageImpl
import com.bobbyesp.spowlo.ui.components.BottomDrawer
import com.bobbyesp.spowlo.ui.components.FilledTonalButtonWithIcon
import com.bobbyesp.spowlo.ui.components.LongTapTextButton
import com.bobbyesp.spowlo.ui.components.MarqueeText
import com.bobbyesp.spowlo.ui.components.MultiChoiceItem
import com.bobbyesp.spowlo.ui.components.OpenInSpotifyFilledButton
import com.bobbyesp.spowlo.ui.components.OutlinedButtonWithIcon
import com.bobbyesp.spowlo.utils.FilesUtil
import com.bobbyesp.spowlo.utils.ToastUtil
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun DownloadHistoryBottomDrawer(downloadsHistoryViewModel: DownloadsHistoryViewModel = hiltViewModel()) {

    val viewState = downloadsHistoryViewModel.detailViewState.collectAsStateWithLifecycle().value
    val scope = rememberCoroutineScope()
    val uriHandler = LocalUriHandler.current
    val context = LocalContext.current
    val hapticFeedback = LocalHapticFeedback.current

    var showDeleteDialog by remember { mutableStateOf(false) }
    var deleteFile by remember { mutableStateOf(false) }

    val shareStringResource = stringResource(id = R.string.share)

    BackHandler(viewState.drawerState.targetValue == ModalBottomSheetValue.Expanded) {
        downloadsHistoryViewModel.hideDrawer(scope)
    }

    LaunchedEffect(viewState.drawerState.targetValue) {
        delay(100)
        showDeleteDialog = false
        deleteFile = false
    }

    with(viewState) {
        DownloadHistoryBottomDrawerImpl(
            drawerState = drawerState,
            songName = title,
            songAuthor = author,
            songUrl = url,
            artworkUrl = artworkUrl,
            onShowDeleteDialog = {
                showDeleteDialog = !showDeleteDialog
            },
            onShareFile = {
                FilesUtil.createIntentForShareAudioFile(path)?.runCatching {
                    context.startActivity(
                        Intent.createChooser(this, shareStringResource)
                    )
                }
            },
            onDeleteCallback = {
                downloadsHistoryViewModel.hideDrawer(scope)
                downloadsHistoryViewModel.removeItem(deleteFile)
            },
            onOpenLink = {
                hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                downloadsHistoryViewModel.hideDrawer(scope)
                uriHandler.openUri(url)
            },
            showDeleteInfo = showDeleteDialog,
            onClickDeleteFile = {
                deleteFile = !deleteFile
            },
            deleteFile = deleteFile
        )
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
    onShowDeleteDialog: () -> Unit,
    onDeleteCallback: () -> Unit,
    onOpenLink: () -> Unit,
    onShareFile: () -> Unit,
    deleteFile: Boolean = false,
    onClickDeleteFile: () -> Unit,
    showDeleteInfo: Boolean = false
) {
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current

    BottomDrawer(
        modifier = Modifier.animateContentSize(),
        drawerState = drawerState,
        sheetContent = {
            AnimatedVisibility(visible = !showDeleteInfo) {
                Column(modifier = Modifier
                    .fillMaxWidth()
                    .animateContentSize()) {
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
                    LongTapTextButton(
                        modifier = Modifier.padding(top = 14.dp),
                        onClick = {
                            clipboardManager.setText(AnnotatedString(songUrl))
                            ToastUtil.makeToast(context.getString(R.string.link_copied))
                        },
                        onClickLabel = stringResource(id = R.string.copy_link),
                        onLongClick = { },
                        onLongClickLabel = stringResource(R.string.open_in_spotify)
                    ) {
                        Icon(Icons.Outlined.Link, stringResource(R.string.song_url))
                        Text(
                            modifier = Modifier
                                .padding(horizontal = 8.dp, vertical = 6.dp),
                            text = songUrl, maxLines = 1, overflow = TextOverflow.Ellipsis
                        )
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                            .padding(top = 24.dp), horizontalArrangement = Arrangement.End
                    ) {
                        OutlinedButtonWithIcon(
                            modifier = Modifier.padding(horizontal = 12.dp),
                            onClick = onShowDeleteDialog,
                            icon = Icons.Outlined.Delete,
                            text = stringResource(R.string.remove)
                        )
                        FilledTonalButtonWithIcon(
                            onClick = onShareFile,
                            icon = Icons.Outlined.Share,
                            text = stringResource(R.string.share_file)
                        )
                        OpenInSpotifyFilledButton(
                            modifier = Modifier.padding(start = 12.dp),
                            onClick = onOpenLink
                        )
                    }
                }
            }
            AnimatedVisibility(visible = showDeleteInfo) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(6.dp)
                        .animateContentSize(),
                    horizontalAlignment = Alignment.Start
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Delete,
                            contentDescription = stringResource(id = R.string.remove),
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(32.dp)
                        )
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 6.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.remove_song),
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = stringResource(R.string.are_you_sure),
                                modifier = Modifier.alpha(alpha = 0.75f),
                                fontSize = 12.sp
                            )
                        }
                    }
                    Text(
                        text = stringResource(R.string.remove_song_info).format(
                            songName,
                            songAuthor
                        ),
                        modifier = Modifier.padding(12.dp),
                        textAlign = TextAlign.Justify
                    )
                    MultiChoiceItem(
                        modifier = Modifier.padding(horizontal = 12.dp),
                        text = stringResource(R.string.delete_file),
                        checked = deleteFile
                    ) { onClickDeleteFile() }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                            .padding(top = 24.dp)
                            .animateContentSize(),
                    ) {
                        OutlinedButtonWithIcon(
                            modifier = Modifier
                                .padding(horizontal = 12.dp)
                                .weight(1f),
                            onClick = {
                                onShowDeleteDialog()
                            },
                            icon = Icons.Outlined.Cancel,
                            text = stringResource(R.string.cancel)
                        )
                        FilledTonalButtonWithIcon(
                            modifier = Modifier
                                .padding(horizontal = 12.dp)
                                .weight(1f),
                            onClick = onDeleteCallback,
                            icon = Icons.Outlined.Delete,
                            text = stringResource(R.string.remove)
                        )

                    }
                }

            }
        })
}