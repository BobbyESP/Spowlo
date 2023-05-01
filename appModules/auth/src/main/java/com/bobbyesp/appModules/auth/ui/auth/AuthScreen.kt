package com.bobbyesp.appModules.auth.ui.auth

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Login
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.neverEqualPolicy
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.autofill.AutofillNode
import androidx.compose.ui.autofill.AutofillType
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.focusTarget
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalAutofill
import androidx.compose.ui.platform.LocalAutofillTree
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.bobbyesp.appModules.auth.R
import com.bobbyesp.appmodules.core.utils.Log
import com.bobbyesp.uisdk.UiUtils

@OptIn(
    ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class,
    ExperimentalLayoutApi::class
)
@Composable
internal fun AuthScreen(
    viewModel: AuthScreenViewModel = hiltViewModel(),
    onShowDisclaimer: () -> Unit,
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
    val validFields = username.isNotEmpty() && password.isNotEmpty()

    var passwordVisible by rememberSaveable { mutableStateOf(false) }

    val gradientHeight = 650f
    val infiniteTransition = rememberInfiniteTransition(label = "Infinite Transition")
    val endY by infiniteTransition.animateFloat(
        initialValue = with(LocalDensity.current) { gradientHeight - 30.dp.toPx() },
        targetValue = with(LocalDensity.current) { gradientHeight + 30.dp.toPx() },
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1200,
                easing = CubicBezierEasing(0.4f, 0f, 0.2f, 1f)
            ),
            repeatMode = RepeatMode.Reverse
        ), label = "Auth Screen Gradient Animation"
    )

    val login = {
        viewModel.auth(
            username = username,
            password = password,
            onSuccess = {
                Log.d("AuthScreen", "Auth Success")
                onProceedToNextStep()
            },
            onFailure = {
                Log.d("AuthScreen", "Auth failed")
                when(it) { //TODO: Add more error handling with localized strings
                    "BadCredentials" -> {
                        setSnackbarContent(it)
                    }
                    else -> {
                        setSnackbarContent(it)
                    }
                }
            }
        )
    }
    val configuration = LocalConfiguration.current
    //val screenWidthDp = configuration.screenWidthDp.dp
    val screenHeightDp = configuration.screenHeightDp.dp

    //val maxXofScreen = with(LocalDensity.current) { screenWidthDp.toPx() }
    val maxYofScreen = with(LocalDensity.current) { screenHeightDp.toPx() }

    val minXofScreen = with(LocalDensity.current) { 0.dp.toPx() }

    val gradientOffset = remember { mutableStateOf(Offset(minXofScreen, maxYofScreen)) }

    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = {
                    Column {
                        Text(text = stringResource(id = R.string.spowlo_login), fontWeight = FontWeight.SemiBold)
                        Text(
                            text = stringResource(id = R.string.spowlo_login_subtitle),
                            style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)),
                            fontWeight = FontWeight.Normal,
                            maxLines = 1
                        )
                    }
                }, navigationIcon = {
                    IconButton(onClick = onBackClicked) {
                        Icon(imageVector = Icons.Rounded.ArrowBack, contentDescription = null)
                    }
                })
        },
        bottomBar = {
            Box(
                contentAlignment = Alignment.BottomCenter,
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
            ) {
                Column(
                    modifier = Modifier.align(Alignment.BottomCenter),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    SnackbarHost(
                        hostState = snackbarHostState,
                        modifier = Modifier,
                        snackbar = { data ->
                            Snackbar(
                                containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(12.dp),
                                contentColor = MaterialTheme.colorScheme.onSurface,
                                snackbarData = data
                            )
                        }
                    )
                    OutlinedButton(
                        onClick = { onShowDisclaimer() },
                        modifier = Modifier
                            .padding(bottom = 16.dp)
                    ) {
                        Text(stringResource(R.string.auth_disclaimer))
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .consumeWindowInsets(innerPadding)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0xFF1DB954),
                            MaterialTheme.colorScheme.background
                        ),
                        center = gradientOffset.value,
                        radius = endY,
                    )
                ),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
                    .padding(innerPadding),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                    ) {
                        Surface(
                            tonalElevation = 16.dp,
                            shape = MaterialTheme.shapes.medium,
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.35f)
                                .compositeOver(MaterialTheme.colorScheme.surfaceVariant),
                            contentColor = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .padding(top = 16.dp)

                        ) {
                            Box(Modifier.padding(12.dp)) {
                                Icon(
                                    UiUtils.localAsset(id = R.drawable.spotify_logo),
                                    contentDescription = "",
                                    modifier = Modifier
                                        .size(84.dp),
                                    tint = MaterialTheme.colorScheme.primary.elevated(elevation = 6.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = stringResource(id = R.string.login_spotify),
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                    }

                }
                Spacer(modifier = Modifier.height(16.dp))
                Autofill(
                    autofillTypes = listOf(AutofillType.EmailAddress, AutofillType.Username),
                    onFill = setUsername
                ) { autofillNode ->
                    OutlinedTextField(
                        value = username,
                        onValueChange = setUsername,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions {
                            passwordFocusRequester.requestFocus()
                        },
                        singleLine = true,
                        label = { Text(stringResource(R.string.username)) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusTarget()
                            .focusRequester(usernameFocusRequester)
                            .onFocusChanged {
                                autofill?.apply {
                                    if (it.isFocused) {
                                        requestAutofillForNode(autofillNode)
                                    } else {
                                        cancelAutofillForNode(autofillNode)
                                    }
                                }
                            }
                            .focusProperties { next = passwordFocusRequester },
                    )
                }

                Autofill(
                    autofillTypes = listOf(AutofillType.Password),
                    onFill = setPassword
                ) { autofillNode ->
                    OutlinedTextField(
                        value = password,
                        onValueChange = setPassword,
                        label = { Text(stringResource(R.string.password)) },
                        singleLine = true,
                        visualTransformation = if (passwordVisible)
                            VisualTransformation.None
                        else
                            PasswordVisualTransformation(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusTarget()
                            .focusRequester(passwordFocusRequester)
                            .onFocusChanged {
                                autofill?.apply {
                                    if (it.isFocused) {
                                        requestAutofillForNode(autofillNode)
                                    } else {
                                        cancelAutofillForNode(autofillNode)
                                    }
                                }
                            }
                            .focusProperties { previous = usernameFocusRequester },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions {
                            focusManager.clearFocus()
                            login()
                        },
                        trailingIcon = {
                            IconButton(
                                onClick = { passwordVisible = !passwordVisible }
                            ) {
                                if (passwordVisible) {
                                    Icon(
                                        Icons.Rounded.Visibility,
                                        stringResource(R.string.hide_password)
                                    )
                                } else {
                                    Icon(
                                        Icons.Rounded.VisibilityOff,
                                        stringResource(R.string.show_password)
                                    )
                                }
                            }
                        }
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp, top = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        onClick = {
                            focusManager.clearFocus()
                            login()
                        },
                        shape = MaterialTheme.shapes.small,
                        modifier = Modifier
                            .weight(1f),
                        enabled = validFields
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.padding(vertical = 4.dp)
                        ) {
                            Text(
                                text = stringResource(id = R.string.login),
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 4.dp)
                            )
                            Icon(
                                imageVector = Icons.Default.Login,
                                contentDescription = "Login",
                                modifier = Modifier.size(24.dp),
                                tint = if (validFields) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface.copy(
                                    alpha = 0.5f
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}


// TODO migrate from Composable wrapper to modifier
@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun Autofill(
    autofillTypes: List<AutofillType>,
    onFill: ((String) -> Unit),
    content: @Composable (AutofillNode) -> Unit
) {
    val autofillNode = AutofillNode(onFill = onFill, autofillTypes = autofillTypes)

    val autofillTree = LocalAutofillTree.current
    autofillTree += autofillNode

    Box(
        modifier = Modifier.onGloballyPositioned {
            autofillNode.boundingBox = it.boundsInWindow()
        }
    ) {
        content(autofillNode)
    }
}


@Composable
// add elevation to a given color
fun Color.elevated(elevation: Dp): Color {
    val alpha = 1 - (elevation.value / 24)
    return copy(alpha = alpha)
}