package com.bobbyesp.spowlo.ui.dialogs.bottomsheets

import android.Manifest
import android.os.Build
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material.icons.outlined.Dataset
import androidx.compose.material.icons.outlined.DownloadDone
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.bobbyesp.spowlo.R
import com.bobbyesp.spowlo.ui.components.FilledButtonWithIcon
import com.bobbyesp.spowlo.ui.components.OutlinedButtonWithIcon
import com.bobbyesp.spowlo.ui.pages.downloader.DownloaderViewModel
import com.bobbyesp.spowlo.utils.ToastUtil
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberPermissionState
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class, ExperimentalPermissionsApi::class)
@Composable
fun DownloaderBottomSheet(
    onBackPressed: () -> Unit,
    downloaderViewModel: DownloaderViewModel,
    navController: NavController
) {
    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState(initialPage = 0)

    val pages = listOf(BottomSheetPages.MAIN, BottomSheetPages.SECONDARY, BottomSheetPages.TERTIARY)
    var selectedTabIndex by remember { mutableStateOf(0) }
    val viewState by downloaderViewModel.viewStateFlow.collectAsStateWithLifecycle()
    val roundedTopShape =
        RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomStart = 0.dp, bottomEnd = 0.dp)

    val storagePermission = rememberPermissionState(
        permission = Manifest.permission.WRITE_EXTERNAL_STORAGE
    ) { b: Boolean ->
        if (b) {
            downloaderViewModel.startDownloadSong()
        } else {
            ToastUtil.makeToast(R.string.permission_denied)
        }
    }

    val checkPermissionOrDownload = {
        if (Build.VERSION.SDK_INT > 29 || storagePermission.status == PermissionStatus.Granted) downloaderViewModel.startDownloadSong()
        else {
            storagePermission.launchPermissionRequest()
        }
    }

    val downloadButtonCallback = {
        navController.popBackStack()
        checkPermissionOrDownload()
    }

    val requestMetadata = {
        navController.popBackStack()
        downloaderViewModel.requestMetadata()
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .navigationBarsPadding()
            .clip(roundedTopShape)
            .padding(8.dp)

    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Outlined.DownloadDone,
                contentDescription = null,
                modifier = Modifier.padding(end = 8.dp, start = 8.dp),
                tint = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = stringResource(R.string.settings_before_download),
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier
                    .padding(vertical = 12.dp),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold
            )
        }
        Text(
            text = stringResource(R.string.settings_before_download_text),
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.align(Alignment.Start).padding(start = 8.dp)
        )
        IndicatorBehindScrollableTabRow(
            selectedTabIndex = pagerState.currentPage,
            indicator = { tabPositions ->
                Box(
                    Modifier
                        .padding(vertical = 12.dp)
                        .tabIndicatorOffset(tabPositions[pagerState.currentPage])
                        .fillMaxHeight()
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.secondaryContainer)
                )
            },
            edgePadding = 16.dp,
            tabAlignment = Alignment.CenterStart,
        ) {
            pages.forEachIndexed { index, page ->
                Tab(
                    text = { Text(text = page) },
                    selected = pagerState.currentPage == index,
                    onClick = {
                        scope.launch {
                            pagerState.animateScrollToPage(index)
                        }
                    },
                )
            }
        }
        HorizontalPager(pageCount = pages.size, state = pagerState) {
            when (pages[it]) {
                BottomSheetPages.MAIN -> {
                    Text(text = "Main page", color = MaterialTheme.colorScheme.onSurface)
                }

                BottomSheetPages.SECONDARY -> {
                    Text(text = "Secondary page", color = MaterialTheme.colorScheme.onSurface)
                }

                BottomSheetPages.TERTIARY -> {
                    Text(text = "Tertiary page", color = MaterialTheme.colorScheme.onSurface)
                }
            }
        }

        val state = rememberLazyListState()

        LaunchedEffect(Unit) {
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
                    onClick = { navController.popBackStack() },
                    icon = Icons.Outlined.Cancel,
                    text = stringResource(R.string.cancel)
                )
            }
            item {
                FilledButtonWithIcon(
                    modifier = Modifier.padding(end = 12.dp),
                    onClick = requestMetadata,
                    icon = Icons.Outlined.Dataset,
                    text = stringResource(R.string.request_metadata)
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
    }
}

object BottomSheetPages {
    const val MAIN = "main"
    const val SECONDARY = "secondary"
    const val TERTIARY = "tertiary"
}
