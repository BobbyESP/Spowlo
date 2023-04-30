package com.bobbyesp.appModules.auth.ui.auth

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.neverEqualPolicy
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalAutofill
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.bobbyesp.appModules.auth.R

@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
@Composable
internal fun AuthScreen(
    viewModel: AuthScreenViewModel = hiltViewModel(),
    onShowDisclaimer : () -> Unit,
    onProceedToNextStep: () -> Unit,
    onBackClicked: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val (snackbarContent, setSnackbarContent) = remember { mutableStateOf("", neverEqualPolicy()) }

    LaunchedEffect(snackbarContent) {
        if (snackbarContent.isNotEmpty()) {
            snackbarHostState.showSnackbar(snackbarContent)
        }
    }

    val autofill = LocalAutofill.current
    val focusManager = LocalFocusManager.current

    val (username, setUsername) = rememberSaveable { mutableStateOf("") }
    val (password, setPassword) = rememberSaveable { mutableStateOf("") }
    val (usernameFocusRequester, passwordFocusRequester) = remember { FocusRequester.createRefs() }

    var passwordVisible by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        topBar = {
            LargeTopAppBar(title = {
                Text(text = stringResource(id = R.string.new_onboarding_sign))
            }, navigationIcon = {
                IconButton(onClick = onBackClicked) {
                    Icon(imageVector = Icons.Rounded.ArrowBack, contentDescription = null)
                }
            })
        }, bottomBar = {

        }
    ) { innerPadding ->
        Text(text = "Sign in", fontSize = 24.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(innerPadding))
    }
}