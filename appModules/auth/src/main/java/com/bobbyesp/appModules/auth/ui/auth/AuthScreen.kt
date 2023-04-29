package com.bobbyesp.appModules.auth.ui.auth

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
@Composable
internal fun AuthScreen(
    viewModel: AuthScreenViewModel = hiltViewModel(),
    onProceedToNextStep: () -> Unit,
    onBackClicked: () -> Unit
) {

}