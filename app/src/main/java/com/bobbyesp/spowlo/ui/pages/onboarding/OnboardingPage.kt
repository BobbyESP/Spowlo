package com.bobbyesp.spowlo.ui.pages.onboarding

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Done
import androidx.compose.material.icons.rounded.DoneAll
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.Start
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bobbyesp.spowlo.R
import com.bobbyesp.spowlo.ui.common.LocalNavController
import com.bobbyesp.spowlo.ui.common.Route
import com.bobbyesp.spowlo.ui.components.buttons.LoginWithSpotify
import com.bobbyesp.spowlo.ui.components.cards.OnboardingCard
import com.bobbyesp.spowlo.ui.pages.LoginManagerViewModel
import com.bobbyesp.spowlo.ui.pages.LoginState
import com.bobbyesp.spowlo.utils.preferences.PreferencesStrings
import com.bobbyesp.spowlo.utils.preferences.PreferencesUtil.updateBoolean

private const val CONTENT_ANIMATION_DURATION = 300

@Composable
fun Onboarding(
    loginAuthManager: LoginManagerViewModel
) {
    val navController = LocalNavController.current

    LaunchedEffect(true) {
        loginAuthManager.getLoggedIn(this)
    }

    var onboardingStep by rememberSaveable(key = "onboardingStep") {
        mutableStateOf(OnboardingStep.WELCOME)
    }

    val onNextStep: () -> Unit = {
        if (onboardingStep.ordinal != OnboardingStep.entries.size - 1) {
            onboardingStep = OnboardingStep.entries[onboardingStep.ordinal + 1]
        }
    }

    val onPreviousStep: () -> Unit = {
        if (onboardingStep.ordinal != 0) {
            onboardingStep = OnboardingStep.entries[onboardingStep.ordinal - 1]
        }
    }

    val existsNextStep = onboardingStep.ordinal != OnboardingStep.entries.size - 1

    if (onboardingStep != OnboardingStep.WELCOME) {
        BackHandler {
            onboardingStep = OnboardingStep.entries[onboardingStep.ordinal - 1]
        }
    }

    val onFinishSetup: () -> Unit = {
        PreferencesStrings.COMPLETED_ONBOARDING.updateBoolean(true)
        //navigate to the HomeNavigator cleaning the backstack
        navController.navigate(Route.HomeNavigator.route) {
            popUpTo(navController.graph.startDestinationId) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    Scaffold(topBar = {
        Column(
            Modifier
                .statusBarsPadding()
                .padding(16.dp)
        ) {
            Text(
                text = stringResource(id = R.string.onboarding_title),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = stringResource(id = R.string.onboarding_desc),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Normal
            )
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
        }
    }, bottomBar = {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .navigationBarsPadding(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            AnimatedContent(
                modifier = Modifier.fillMaxWidth(),
                targetState = onboardingStep,
                label = "Bottom bar buttons animated content"
            ) { step ->
                when (step) {
                    OnboardingStep.WELCOME -> {
                        FilledTonalButton(
                            onClick = onNextStep,
                            modifier = Modifier
                                .weight(1f)
                                .animateContentSize(),
                            contentPadding = PaddingValues(16.dp),
                            shape = MaterialTheme.shapes.medium
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Start,
                                contentDescription = stringResource(id = R.string.start)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(stringResource(id = R.string.start))
                        }
                    }

                    OnboardingStep.LOGIN -> {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            FilledTonalButton(
                                onClick = onPreviousStep,
                                modifier = Modifier
                                    .weight(1f)
                                    .animateContentSize(),
                                contentPadding = PaddingValues(16.dp),
                                shape = MaterialTheme.shapes.medium
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                                    contentDescription = stringResource(id = R.string.back)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(stringResource(id = R.string.back))
                            }
                            FilledTonalButton(
                                onClick = onNextStep,
                                modifier = Modifier
                                    .weight(1f)
                                    .animateContentSize(),
                                contentPadding = PaddingValues(16.dp),
                                enabled = existsNextStep,
                                shape = MaterialTheme.shapes.medium
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Start,
                                    contentDescription = stringResource(id = R.string.next_step)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(stringResource(id = R.string.next_step))
                            }
                        }
                    }

                    OnboardingStep.FINISH -> {
                        FilledTonalButton(
                            onClick = onFinishSetup,
                            modifier = Modifier
                                .weight(1f)
                                .animateContentSize(),
                            contentPadding = PaddingValues(16.dp),
                            shape = MaterialTheme.shapes.medium
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Start,
                                contentDescription = stringResource(id = R.string.finish)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(stringResource(id = R.string.finish))
                        }
                    }
                }
            }

        }
    }) { innerPadding ->
        AnimatedContent(
            targetState = onboardingStep, transitionSpec = {
                if (targetState.ordinal > initialState.ordinal) {
                    slideIntoContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Left,
                        animationSpec = tween(CONTENT_ANIMATION_DURATION),
                    ) togetherWith fadeOut(
                        animationSpec = tween(CONTENT_ANIMATION_DURATION)
                    )
                } else {
                    fadeIn(
                        animationSpec = tween(CONTENT_ANIMATION_DURATION),
                    ) togetherWith slideOutOfContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Right,
                        animationSpec = tween(CONTENT_ANIMATION_DURATION)
                    )
                }.apply {
                    targetContentZIndex = targetState.ordinal.toFloat()
                }
            }, label = "Onboarding animated content transition"
        ) { step ->
            when (step) {
                OnboardingStep.WELCOME -> {
                    Welcome(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    )
                }

                OnboardingStep.LOGIN -> {
                    Login(modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding), onLogin = {
                        loginAuthManager.login()
                        PreferencesStrings.SKIPPED_LOGIN.updateBoolean(false)
                    }, onSkip = {
                        PreferencesStrings.SKIPPED_LOGIN.updateBoolean(true)
                        onNextStep()
                    }, loginViewModel = loginAuthManager
                    )
                }

                OnboardingStep.FINISH -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            modifier = Modifier
                                .padding(16.dp)
                                .background(MaterialTheme.colorScheme.surfaceColorAtElevation(12.dp))
                                .size(72.dp),
                            imageVector = Icons.Rounded.Done,
                            contentDescription = null
                        )
                        Text(
                            text = stringResource(id = R.string.onboarding_finish_title),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = stringResource(id = R.string.onboarding_finish_desc),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Normal
                        )
                    }

                }
            }
        }
    }
}

@Composable
private fun Welcome(
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            OnboardingCard(
                icon = {
                    Icon(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(MaterialTheme.shapes.small),
                        imageVector = ImageVector.vectorResource(id = R.drawable.ic_launcher_foreground),
                        contentDescription = stringResource(id = R.string.onboard_icon),
                        tint = MaterialTheme.colorScheme.primary
                    )
                },
                title = stringResource(id = R.string.what_is_spowlo),
                text = stringResource(id = R.string.what_is_spowlo_desc)
            )
        }
        item {
            OnboardingCard(
                icon = Icons.Rounded.Star,
                title = stringResource(id = R.string.access_account),
                text = stringResource(id = R.string.access_account_desc)
            )
        }
    }
}

@Composable
private fun Login(
    modifier: Modifier = Modifier,
    onLogin: () -> Unit,
    onSkip: () -> Unit,
    loginViewModel: LoginManagerViewModel
) {
    val loginManagerState by loginViewModel.pageViewState.collectAsStateWithLifecycle()

    Crossfade(
        targetState = loginManagerState.loginState,
        animationSpec = tween(300),
        label = "Crossfade login state animation"
    ) { loginState ->
        when (loginState) {
            LoginState.LOGGED_IN -> {
                IsLoggedStage(modifier)
            }

            LoginState.NOT_LOGGED_IN -> {
                AwaitingLogin(modifier, onLogin, onSkip)
            }

            LoginState.CHECKING_STATUS -> {
                Box(
                    modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = stringResource(id = R.string.trying_to_login),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

@Composable
private fun IsLoggedStage(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.primary)
                    .clip(MaterialTheme.shapes.small)
                    .padding(16.dp)
                    .size(72.dp),
                imageVector = Icons.Rounded.DoneAll,
                contentDescription = stringResource(id = R.string.logged_in),
                tint = MaterialTheme.colorScheme.onPrimary
            )
            Text(
                text = stringResource(id = R.string.logged_in_desc),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun AwaitingLogin(
    modifier: Modifier = Modifier, onLogin: () -> Unit, onSkip: () -> Unit
) {
    Column(
        modifier = modifier.padding(horizontal = 12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.primary)
                .clip(MaterialTheme.shapes.small)
                .padding(16.dp)
                .size(72.dp),
            imageVector = ImageVector.vectorResource(id = R.drawable.spotify_logo),
            contentDescription = "Spotify logo",
            tint = MaterialTheme.colorScheme.onPrimary
        )
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(id = R.string.login_with_spotify),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = stringResource(id = R.string.login_with_spotify_description),
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            )
        }
        LoginWithSpotify(
            modifier = Modifier.fillMaxWidth()
        ) {
            onLogin()
        }
        OutlinedButton(
            modifier = Modifier.fillMaxWidth(),
            onClick = onSkip,
            contentPadding = PaddingValues(16.dp),
            shape = MaterialTheme.shapes.small
        ) {
            Text(text = stringResource(id = R.string.preferred_to_skip))
        }
    }
}

enum class OnboardingStep {
    WELCOME, LOGIN, FINISH
}