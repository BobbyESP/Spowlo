package com.bobbyesp.spowlo.utils.ui.pages.update

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.Label
import androidx.compose.material.icons.outlined.NewReleases
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bobbyesp.spowlo.R
import com.bobbyesp.spowlo.ui.components.buttons.FilledTonalButtonWithIcon
import com.bobbyesp.spowlo.ui.components.buttons.OutlinedButtonWithIcon
import com.bobbyesp.spowlo.ui.components.dividers.HorizontalDivider
import com.bobbyesp.spowlo.ui.components.others.tags.ChangelogTag
import com.bobbyesp.spowlo.ui.components.others.tags.CustomTag
import com.bobbyesp.spowlo.ui.theme.SpowloTheme
import com.bobbyesp.spowlo.utils.UpdateUtil
import com.bobbyesp.spowlo.utils.time.TimeUtils
import dev.jeziellago.compose.markdowntext.MarkdownText


@Composable
fun UpdatePage(
    onDismissRequest: () -> Unit,
    onConfirmUpdate: () -> Unit,
    latestRelease: UpdateUtil.LatestRelease,
    downloadStatus: UpdateUtil.DownloadStatus,
) {
    val uriHandler = LocalUriHandler.current

    fun openUrl(url: String) {
        uriHandler.openUri(url)
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            HorizontalDivider()
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AnimatedVisibility(
                    visible = downloadStatus is UpdateUtil.DownloadStatus.Progress
                ) {
                    LinearProgressIndicator(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(6.dp),
                        progress = when (downloadStatus) {
                            is UpdateUtil.DownloadStatus.Progress -> downloadStatus.percent.toFloat() / 100f
                            else -> 0f
                        }
                    )
                }
                FilledTonalButtonWithIcon(
                    modifier = Modifier
                        .fillMaxWidth(),
                    onClick = onConfirmUpdate,
                    icon = Icons.Outlined.Download,
                    text = stringResource(R.string.update)
                )
                OutlinedButtonWithIcon(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 6.dp),
                    onClick = onDismissRequest,
                    icon = Icons.Outlined.Cancel,
                    text = stringResource(R.string.cancel)
                )
            }
        }
    ) {
        Column(
            modifier = Modifier
                .padding(it)
                .padding(horizontal = 16.dp)
                .fillMaxSize(),
        ) {
            Column(
                modifier = Modifier
                    .padding(top = 4.dp, bottom = 8.dp)
                    .fillMaxWidth()
            ) {
                Icon(
                    modifier = Modifier
                        .size(52.dp)
                        .padding(vertical = 6.dp),
                    imageVector = Icons.Outlined.NewReleases,
                    contentDescription = "New release icon for update page",
                    tint = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = stringResource(id = R.string.new_version_available),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                item {
                    CustomTag(
                        text = latestRelease.tagName.toString(),
                        icon = Icons.Outlined.Label,
                    )
                }
                item {
                    ChangelogTag()
                }
                item {
                    CustomTag(
                        text = TimeUtils.parseDateStringToLocalTime(latestRelease.publishedAt.toString())
                            ?: latestRelease.publishedAt.toString(),
                        icon = Icons.Outlined.CalendarMonth,
                    )
                }
            }
            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .fillMaxWidth()
            ) {
                MarkdownText(
                    modifier = Modifier
                        .fillMaxWidth(),
                    markdown = latestRelease.body.toString(),
                    textAlign = TextAlign.Justify,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                    onLinkClicked = { url ->
                        openUrl(url)
                    }
                )
            }
        }
    }
}

//Thx ChatGPT
val fakeData = UpdateUtil.LatestRelease(
    htmlUrl = "https://github.com/username/repo/releases/tag/v1.0",
    tagName = "v1.0",
    name = "Release 1.0",
    draft = false,
    preRelease = false,
    createdAt = "2023-05-28T10:15:00Z",
    publishedAt = "2023-05-28T12:30:00Z",
    assets = listOf(
        UpdateUtil.AssetsItem(
            name = "app.apk",
            contentType = "application/vnd.android.package-archive",
            size = 1024,
            downloadCount = 100,
            createdAt = "2023-05-28T12:30:00Z",
            updatedAt = "2023-05-28T12:35:00Z",
            browserDownloadUrl = "https://github.com/username/repo/releases/download/v1.0/app.apk"
        ),
        UpdateUtil.AssetsItem(
            name = "changelog.txt",
            contentType = "text/plain",
            size = 512,
            downloadCount = 50,
            createdAt = "2023-05-28T12:32:00Z",
            updatedAt = "2023-05-28T12:34:00Z",
            browserDownloadUrl = "https://github.com/username/repo/releases/download/v1.0/changelog.txt"
        )
    ),
    body = "This is the release description. Here all the text is going to appear"
)

@Preview
@Preview(uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun UpdatePagePreview() {
    SpowloTheme {
        UpdatePage(
            onDismissRequest = {},
            onConfirmUpdate = {},
            latestRelease = fakeData,
            downloadStatus = UpdateUtil.DownloadStatus.NotYet
        )
    }
}