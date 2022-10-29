package com.bobbyesp.spowlo.presentation.ui.pages.settings.about

import android.content.pm.PackageManager
import android.os.Build
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import kotlin.math.roundToInt

private const val releaseURL = "https://github.com/BobbyESP/Spowlo/releases"
private const val repoUrl = "https://github.com/BobbyESP/Spowlo"
private const val githubIssueUrl = "https://github.com/BobbyESP/Spowlo/issues/new/choose"

private const val TAG = "AboutPage"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutPage(onBackPressed: () -> Unit, jumpToCreditsPage: () -> Unit){
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        rememberTopAppBarState(),
        canScroll = { true })
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val configuration = LocalConfiguration.current
    val screenDensity = configuration.densityDpi / 160f
    val screenHeight = (configuration.screenHeightDp.toFloat() * screenDensity).roundToInt()
    val screenWidth = (configuration.screenWidthDp.toFloat() * screenDensity).roundToInt()
    val info = if (Build.VERSION.SDK_INT >= 33) context.packageManager.getPackageInfo(
        context.packageName, PackageManager.PackageInfoFlags.of(0)
    )
    else context.packageManager.getPackageInfo(context.packageName, 0)

    val versionName = info.versionName

    val versionCode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        info.longVersionCode
    } else {
        info.versionCode.toLong()
    }
    val release = if (Build.VERSION.SDK_INT >= 30) {
        Build.VERSION.RELEASE_OR_CODENAME
    } else {
        Build.VERSION.RELEASE
    }

    val infoBuilder = StringBuilder()
    val deviceInformation =
        infoBuilder.append("App version: $versionName")
            .append(" ($versionCode)\n")
            .append("Device information: Android $release (API ${Build.VERSION.SDK_INT})\n")
            .append(Build.SUPPORTED_ABIS.contentToString())
            .append("\nScreen resolution: $screenHeight x $screenWidth").toString()
    val uriHandler = LocalUriHandler.current
    fun openUrl(url: String) {
        uriHandler.openUri(url)
    }
}