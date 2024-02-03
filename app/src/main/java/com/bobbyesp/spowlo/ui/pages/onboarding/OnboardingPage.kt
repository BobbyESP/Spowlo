package com.bobbyesp.spowlo.ui.pages.onboarding

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.Start
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.bobbyesp.spowlo.R
import com.bobbyesp.spowlo.ui.components.cards.OnboardingCard

private const val CONTENT_ANIMATION_DURATION = 300

@Composable
fun Onboarding(
    viewModel: OnboardingViewModel
) {
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
                modifier = Modifier
                    .fillMaxWidth(),
                targetState = onboardingStep,
                label = "Bottom bar buttons animated content") { step ->
                when(step) {
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
                    OnboardingStep.DONE -> {
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

                OnboardingStep.DONE -> {
                    Text(text = "Done")
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