package com.bobbyesp.spowlo.ui.components.navigation

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Stable
import androidx.compose.ui.unit.Dp
import androidx.navigation.NavHostController
import com.bobbyesp.ui.components.bottomsheet.draggable.DraggableBottomSheetState

data class NavigationBarsProperties(
    val currentRootRoute: MutableState<String>,
    @Stable val navController: NavHostController,
    val navBarCurrentHeight: Dp,
    val neededInset: Dp,
    val playerBottomSheetState: DraggableBottomSheetState
)