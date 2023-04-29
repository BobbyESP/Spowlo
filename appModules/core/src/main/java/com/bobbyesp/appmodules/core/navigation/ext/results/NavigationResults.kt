package com.bobbyesp.appmodules.core.navigation.ext.results

import android.os.Parcelable
import androidx.compose.runtime.Stable

@Stable
interface NavigationResult: Parcelable{

/*fun NavController.setResultToPreviousEntry(result: NavigationResult, popBackStack: Boolean = true) {
    previousBackStackEntry?.savedStateHandle?.set(CommonArgs.Result.name, result)

    if (popBackStack) {
        popBackStack()
    }
}

@Composable
fun InstallResultHandler(backStack: NavBackStackEntry, block: suspend CoroutineScope.(NavigationResult) -> Unit) {
    val resultState = backStack.savedStateHandle.getStateFlow<NavigationResult?>(CommonArgs.Result.name, null).collectAsState()

    LaunchedEffect(resultState) {
        block(resultState.value ?: return@LaunchedEffect)
        backStack.savedStateHandle[CommonArgs.Result.name] = null
    }
}

@Composable
inline fun <reified T: NavigationResult> InstallTypedResultHandler(backStack: NavBackStackEntry, crossinline block: suspend CoroutineScope.(T) -> Unit) {
    InstallResultHandler(backStack) { result ->
        block((result as? T) ?: return@InstallResultHandler)
    }*/
}