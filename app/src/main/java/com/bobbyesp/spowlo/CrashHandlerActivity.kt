package com.bobbyesp.spowlo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BugReport
import androidx.compose.material.icons.outlined.PermDeviceInformation
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import com.bobbyesp.spowlo.ui.common.AppLocalSettingsProvider
import com.bobbyesp.spowlo.ui.common.LocalDarkTheme
import com.bobbyesp.spowlo.ui.common.LocalDynamicColorSwitch
import com.bobbyesp.spowlo.ui.components.buttons.FilledButtonWithIcon
import com.bobbyesp.spowlo.ui.components.cards.ExpandableElevatedCard
import com.bobbyesp.spowlo.ui.components.dividers.HorizontalDivider
import com.bobbyesp.spowlo.ui.theme.SpowloTheme
import com.bobbyesp.spowlo.utils.localAsset

class CrashHandlerActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        ViewCompat.setOnApplyWindowInsetsListener(window.decorView) { v, insets ->
            v.setPadding(0, 0, 0, 0)
            insets
        }
        val errorMessage: String = intent.getStringExtra("error_report").toString()
        val versionReport: String = intent.getStringExtra("version_report").toString()

        setContent {
            AppLocalSettingsProvider(WindowWidthSizeClass.Compact) {
                SpowloTheme(
                    darkTheme = LocalDarkTheme.current.isDarkTheme(),
                    isHighContrastModeEnabled = LocalDarkTheme.current.isHighContrastModeEnabled,
                    isDynamicColorEnabled = LocalDynamicColorSwitch.current,
                ) {
                    val clipboardManager = LocalClipboardManager.current
                    CrashReportPage(
                        versionReport, errorMessage
                    ) {
                        clipboardManager.setText(AnnotatedString(versionReport).plus(AnnotatedString(errorMessage)))
                        this.finishAffinity()
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isFinishing) finishAffinity()
    }
}

@Composable
@Preview
fun CrashReportPage(
    versionReport: String = "VERSION REPORT",
    errorMessage: String = error_report_fake,
    onClick: () -> Unit = {}
) {
    Scaffold(modifier = Modifier.fillMaxSize(), bottomBar = {
        HorizontalDivider()
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(vertical = 8.dp)
        ) {
            FilledButtonWithIcon(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .weight(1f),
                onClick = onClick,
                icon = Icons.Outlined.BugReport,
                text = stringResource(R.string.copy_and_exit)
            )
            FilledButtonWithIcon(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .weight(1f),
                onClick = onClick,
                icon = localAsset(id = R.drawable.github_mark),
                text = stringResource(R.string.report_github)
            )
        }
    }) {
        Column(
            modifier = Modifier
                .padding(it)
                .verticalScroll(rememberScrollState())
                .fillMaxSize()
        ) {
            Icon(
                imageVector = Icons.Outlined.BugReport,
                contentDescription = "Bug occurred icon",
                modifier = Modifier
                    .padding(start = 16.dp)
                    .padding(top = 16.dp)
                    .size(48.dp)
            )
            Text(
                text = stringResource(R.string.unknown_error_title),
                style = MaterialTheme.typography.displaySmall,
                modifier = Modifier
                    .padding(top = 16.dp, bottom = 12.dp)
                    .padding(horizontal = 16.dp)
            )
            ExpandableElevatedCard(
                modifier = Modifier.padding(16.dp),
                title = stringResource(id = R.string.device_info),
                subtitle = stringResource(
                    id = R.string.device_info_subtitle
                ),
                icon = Icons.Outlined.PermDeviceInformation
            ) {
                SelectionContainer {
                    Text(
                        text = versionReport,
                        style = MaterialTheme.typography.bodyMedium.copy(fontFamily = FontFamily.Monospace),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                            .fillMaxWidth()
                    )
                }
            }

            HorizontalDivider(modifier = Modifier.padding(horizontal = 8.dp))
            SelectionContainer {
                Text(
                    text = errorMessage,
                    style = MaterialTheme.typography.bodyMedium.copy(fontFamily = FontFamily.Monospace),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                )
            }
        }
    }
}

private const val error_report_fake = """java.lang.Exception: Error while initializing Python interpreter: Cannot run program "" : error=2, No such file or directory
	at com.bobbyesp.spotdl_android.SpotDL.init(SpotDL.kt:64)
	at com.bobbyesp.spowlo.App$'$'}2.invokeSuspend(App.kt:43)
	at kotlin.coroutines.jvm.internal.BaseContinuationImpl.resumeWith(ContinuationImpl.kt:33)
	at kotlinx.coroutines.DispatchedTask.run(DispatchedTask.kt:106)
	at kotlinx.coroutines.internal.LimitedDispatcherWorker.run(LimitedDispatcher.kt:115)
	at kotlinx.coroutines.scheduling.TaskImpl.run(Tasks.kt:100)
	at kotlinx.coroutines.scheduling.CoroutineScheduler.runSafely(CoroutineScheduler.kt:584)
	at kotlinx.coroutines.scheduling.CoroutineSchedulerWorker.executeTask(CoroutineScheduler.kt:793)
	at kotlinx.coroutines.scheduling.CoroutineSchedulerWorker.runWorker(CoroutineScheduler.kt:697)
	at kotlinx.coroutines.scheduling.CoroutineSchedulerWorker.run(CoroutineScheduler.kt:684)"""