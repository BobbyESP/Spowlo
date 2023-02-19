package com.bobbyesp.spowlo.ui.pages.settings.about

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Newspaper
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import com.bobbyesp.spowlo.App
import com.bobbyesp.spowlo.App.Companion.packageInfo
import com.bobbyesp.spowlo.R
import com.bobbyesp.spowlo.ui.components.BackButton
import com.bobbyesp.spowlo.ui.components.HorizontalDivider
import com.bobbyesp.spowlo.ui.components.LargeTopAppBar
import com.bobbyesp.spowlo.ui.components.PreferenceItem
import com.bobbyesp.spowlo.ui.components.PreferenceSubtitle
import com.bobbyesp.spowlo.ui.components.about.ContributorComponent
import com.bobbyesp.spowlo.ui.components.about.HeadDeveloperComponent
import com.bobbyesp.spowlo.utils.ChromeCustomTabsUtil
import com.bobbyesp.spowlo.utils.ToastUtil

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutPage(onBackPressed: () -> Unit) {

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        rememberTopAppBarState(),
        canScroll = { true }
    )

    val BobbyESPLogoUrl = "https://avatars.githubusercontent.com/u/60316747"
    val BobbyESPGHUrl = "https://github.com/BobbyESP"

    val spowloRepo = "https://github.com/BobbyESP/Spowlo"

    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    val info = App.getVersionReport()
    val versionName = packageInfo.versionName

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(
                        modifier = Modifier,
                        text = stringResource(id = R.string.about),
                    )
                }, navigationIcon = {
                    BackButton {
                        onBackPressed()
                    }
                }, scrollBehavior = scrollBehavior
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                PreferenceSubtitle(
                    modifier = Modifier.padding(bottom = 6.dp),
                    text = stringResource(id = R.string.head_developer)
                )
                HeadDeveloperComponent(
                    name = "BobbyESP",
                    description = "A passionated teenager developer",
                    logoUrl = BobbyESPLogoUrl,
                    githubUrl = BobbyESPGHUrl
                )
            }

            item {
                PreferenceSubtitle(text = stringResource(id = R.string.contributors))
                Column(
                    modifier = Modifier.padding(top = 6.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    ContributorComponent(
                        name = "xnetcat",
                        description = "The main developer of the Python spotDL library",
                        avatarUrl = "https://avatars.githubusercontent.com/u/42355410",
                        socialUrl = "https://github.com/xnetcat",
                        socialNetworkImage = ImageVector.vectorResource(id = R.drawable.github_mark)
                    )
                    ContributorComponent(
                        name = "decipher",
                        description = "A nice artist that created our logo!",
                        avatarUrl = "https://i.ibb.co/Y8G4SZg/image.png",
                        socialUrl = "https://t.me/decipher3114",
                        socialNetworkImage = ImageVector.vectorResource(id = R.drawable.telegram_icon)
                    )
                    ContributorComponent(
                        name = "Wolf 🐺",
                        description = "A nice tester and helper",
                        avatarUrl = "https://i.ibb.co/3zzdbq7/wolf-avatar.png"
                    )
                    ContributorComponent(
                        name = "Katoka",
                        description = "The app name creator! Nice words game haha"
                    )
                    HorizontalDivider()
                }
            }
            item {
                PreferenceItem(
                    title = stringResource(id = R.string.telegram_channel),
                    description = stringResource(
                        id = R.string.join_telegram_channel
                    ),
                    icon = LocalAsset(id = R.drawable.telegram_icon)
                ) {
                    ChromeCustomTabsUtil.openUrl("https://t.me/spowlo_chatroom")
                }
            }
            item {
                PreferenceItem(
                    title = stringResource(R.string.source_code),
                    description = stringResource(id = R.string.check_source_code),
                    icon = LocalAsset(id = R.drawable.github_mark)
                ) {
                    ChromeCustomTabsUtil.openUrl(spowloRepo)
                }
            }
            item {
                PreferenceItem(
                    title = stringResource(R.string.report_issue),
                    description = stringResource(id = R.string.report_issue_description),
                    icon = Icons.Filled.Error
                ) {
                    ChromeCustomTabsUtil.openUrl("$spowloRepo/issues/new")
                }
            }
            item {
                PreferenceItem(
                    title = stringResource(R.string.feature_request),
                    description = stringResource(id = R.string.feature_request_description),
                    icon = Icons.Filled.Newspaper
                ) {
                    ChromeCustomTabsUtil.openUrl("$spowloRepo/issues/new")
                }

            }
            item {
                PreferenceItem(
                    title = stringResource(R.string.version),
                    description = versionName,
                    icon = Icons.Outlined.Info,
                ) {
                    clipboardManager.setText(AnnotatedString(info))
                    ToastUtil.makeToast(R.string.copied_to_clipboard)
                }
            }
        }

    }
}

@Composable
fun LocalAsset(@DrawableRes id: Int) = ImageVector.vectorResource(id = id)