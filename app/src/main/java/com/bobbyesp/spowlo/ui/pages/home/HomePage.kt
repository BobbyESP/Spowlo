package com.bobbyesp.spowlo.ui.pages.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.bobbyesp.spowlo.ui.common.LocalNavController
import com.bobbyesp.spowlo.ui.common.Route
import com.bobbyesp.spowlo.ui.components.topbars.SmallTopAppBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomePage() {
    val navController = LocalNavController.current
    Scaffold(
        topBar = {
            SmallTopAppBar(
                modifier = Modifier.fillMaxWidth(),
                title = {
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            navController.navigate(Route.SettingsNavigator.route)
                        }
                    ){
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings Icon",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize().padding(paddingValues)
        ) {
            Text(text = "Home Page")
        }
    }
}