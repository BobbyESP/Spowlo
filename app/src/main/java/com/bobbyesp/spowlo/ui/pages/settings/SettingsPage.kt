package com.bobbyesp.spowlo.ui.pages.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.bobbyesp.spowlo.R
import com.bobbyesp.spowlo.ui.common.LocalNavController
import com.bobbyesp.spowlo.ui.components.buttons.BackButton
import com.bobbyesp.spowlo.ui.components.topbars.LargeTopAppBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsPage() {

    val navController = LocalNavController.current

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            LargeTopAppBar(navigationIcon = {
                BackButton {
                    navController.popBackStack()
                }
            }, title = {
                Text(
                    text = stringResource(id = R.string.settings),
                    style = MaterialTheme.typography.displaySmall
                )
            }, scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
            )
        },
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

        }
    }
}