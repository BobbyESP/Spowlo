package com.bobbyesp.spowlo.presentation.ui.components.bottomNavBar

import androidx.compose.ui.graphics.vector.ImageVector

data class NavBarItem(
    val name: String,
    val icon: ImageVector,
    val route: String,
    val badgeCount: Int = 0
)
