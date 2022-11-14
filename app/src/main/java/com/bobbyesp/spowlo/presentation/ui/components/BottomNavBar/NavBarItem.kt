package com.bobbyesp.spowlo.presentation.ui.components.BottomNavBar

import androidx.compose.ui.graphics.vector.ImageVector
import com.bobbyesp.spowlo.presentation.ui.common.Route

data class NavBarItem(
    val name: String,
    val icon: ImageVector,
    val route: String,
    val badgeCount: Int = 0
)
