package com.bobbyesp.spowlo.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.bobbyesp.ui.components.bottomsheet.draggable.DraggableBottomSheet
import com.bobbyesp.ui.components.bottomsheet.draggable.DraggableBottomSheetState

@Composable
fun PlayerBottomSheet(
    modifier: Modifier = Modifier, state: DraggableBottomSheetState
) {
    DraggableBottomSheet(
        state = state,
        collapsedContent = {

        },
        backgroundColor = MaterialTheme.colorScheme.surfaceColorAtElevation(NavigationBarDefaults.Elevation)
    ) {

    }
}