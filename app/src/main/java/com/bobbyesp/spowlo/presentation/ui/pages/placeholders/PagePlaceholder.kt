package com.bobbyesp.spowlo.presentation.ui.pages.placeholders

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PagePlaceholder(navController: NavController) {
    Surface(modifier = Modifier.fillMaxSize()) {
        Row(modifier = Modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center) {

            Text(modifier = Modifier,
            text = "Page not implemented yet",
            style = MaterialTheme.typography.titleLarge)
        }
    }
}