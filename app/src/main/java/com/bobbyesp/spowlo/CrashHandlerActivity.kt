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

        setContent {
            AppLocalSettingsProvider(WindowWidthSizeClass.Compact) {
                SpowloTheme(
                    darkTheme = LocalDarkTheme.current.isDarkTheme(),
                    isHighContrastModeEnabled = LocalDarkTheme.current.isHighContrastModeEnabled,
                    isDynamicColorEnabled = LocalDynamicColorSwitch.current,
                ) {
                    val clipboardManager = LocalClipboardManager.current
                    CrashReportPage(errorMessage = errorMessage) {
                        clipboardManager.setText(AnnotatedString(errorMessage))
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
fun CrashReportPage(errorMessage: String = "ERROR_EXAMPLE", onClick: () -> Unit = {}) {
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
        ) {
            Icon(
                imageVector = Icons.Outlined.BugReport,
                contentDescription = "Bug occurred",
                modifier = Modifier.padding(start = 16.dp).padding(top = 16.dp).size(48.dp)
            )
            Text(
                text = stringResource(R.string.unknown_error_title),
                style = MaterialTheme.typography.displaySmall,
                modifier = Modifier
                    .padding(top = 16.dp, bottom = 12.dp)
                    .padding(horizontal = 16.dp)
            )
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