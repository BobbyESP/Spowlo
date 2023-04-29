package com.bobbyesp.spowlo.ui.navigation

import androidx.annotation.StringRes
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.navigation.NavHostController
import com.bobbyesp.spowlo.ui.pages.BottomSheet
import com.bobbyesp.spowlo.ui.pages.Dialog
import com.bobbyesp.spowlo.ui.pages.Screen
import com.bobbyesp.spowlo.utils.ChromeCustomTabsUtil
import javax.annotation.concurrent.Immutable

@JvmInline
@Immutable
value class NavigationController(
    val controller: () -> NavHostController
) {
    // Not recommended
    fun navigate(route: String) = controller().navigate(route)

    fun navigate(screen: Screen) = controller().navigate(screen.route)
    fun navigate(dialog: Dialog) = controller().navigate(dialog.route)

    fun navigate(sheet: BottomSheet, args: Map<String, String>) {
        var url = sheet.route

        args.forEach { entry ->
            url = url.replace("{${entry.key}}", entry.value)
        }

        controller().navigate(url)
    }

    fun navigateAndClearStack(screen: Screen) = controller().navigate(screen.route) { popUpTo(Screen.NavGraph.route) }

    fun popBackStack() = controller().popBackStack()

    fun context() = controller().context
    fun string(@StringRes id: Int) = context().getString(id)
    fun openInBrowser(uri: String) = ChromeCustomTabsUtil.openUrl(uri)
}

val LocalNavigationController = staticCompositionLocalOf<NavigationController> { error("Supply NavigationController in composable scope!") }