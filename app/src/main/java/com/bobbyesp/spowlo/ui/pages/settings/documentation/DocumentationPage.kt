package com.bobbyesp.spowlo.ui.pages.settings.documentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.bobbyesp.spowlo.R
import com.bobbyesp.spowlo.ui.components.BackButton
import com.bobbyesp.spowlo.ui.components.HorizontalDivider
import com.bobbyesp.spowlo.ui.components.InlineEnterItem
import com.bobbyesp.spowlo.ui.components.LargeTopAppBar
import com.bobbyesp.spowlo.ui.components.PreferenceInfo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DocumentationPage(
    onBackPressed: () -> Unit,
    navController: NavController
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        rememberTopAppBarState(),
        canScroll = { true }
    )

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(
                        modifier = Modifier,
                        text = stringResource(id = R.string.documentation),
                        fontWeight = FontWeight.Bold
                    )
                }, navigationIcon = {
                    BackButton {
                        onBackPressed()
                    }
                }, scrollBehavior = scrollBehavior
            )
        }, content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                InlineEnterItem(title = stringResource(id = R.string.index)) {
                    val uri = "markdown_viewer/index.md"
                    navController.navigate(uri)
                }
                InlineEnterItem(title = stringResource(id = R.string.commands)) {
                    val uri = "markdown_viewer/cli_commands.md"
                    navController.navigate(uri)
                }

                HorizontalDivider(Modifier.padding(vertical = 6.dp))
                PreferenceInfo(
                    modifier = Modifier
                        .padding(horizontal = 4.dp),
                    text = stringResource(id = R.string.documentation_info)
                )
            }
        }
    )
}
