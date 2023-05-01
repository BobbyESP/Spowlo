package com.bobbyesp.appmodules.hub.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.bobbyesp.appmodules.hub.ui.screens.core.HubScreen
import com.bobbyesp.uisdk.components.BackButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GenrePage(
    id: String,
    onBackPressed: () -> Unit
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    var appBarTitle by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            LargeTopAppBar(title = {
                Text(appBarTitle, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }, navigationIcon = {
                BackButton {
                    onBackPressed()
                }
            }, colors = TopAppBarDefaults.largeTopAppBarColors(),
                scrollBehavior = scrollBehavior
            )
        },
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        contentWindowInsets = WindowInsets(top = 0.dp)
    ) { padding ->
        Box(Modifier.padding(padding)) {
            HubScreen(
                needContentPadding = false,
                loader = { getBrowseView(id) },
                onAppBarTitleChange = { appBarTitle = it }
            )
        }
    }
}