package com.bobbyesp.spowlo.ui.pages.onboarding

import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun OnboardingPage(
    viewModel: OnboardingViewModel,
    onFinished: () -> Unit,
) {
    val pageState = viewModel.pageViewState.collectAsStateWithLifecycle()


//    AnimatedContent(targetState = Unit, transitionSpec = {
//        if (targetState.questionIndex > initialState.questionIndex) {
//            // Going forwards in the survey
//            slideIntoContainer(
//                towards = AnimatedContentScope.SlideDirection.Left,
//                animationSpec = tween(600),
//            ) with fadeOut(
//                animationSpec = tween(600)
//            )
//        } else {
//            // Going back to the previous question in the set
//            fadeIn(
//                animationSpec = tween(600),
//            ) with slideOutOfContainer(
//                towards = AnimatedContentScope.SlideDirection.Right, animationSpec = tween(600)
//            )
//        }.apply {
//            targetContentZIndex = targetState.questionIndex.toFloat()
//        }
//    }) {
//
//    }
}