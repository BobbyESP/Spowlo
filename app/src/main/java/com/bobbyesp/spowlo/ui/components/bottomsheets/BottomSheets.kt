package com.bobbyesp.spowlo.ui.components.bottomsheets

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.t8rin.modalsheet.ModalSheet
import com.t8rin.modalsheet.ModalSheetState


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModernModalBottomSheet(
    modifier: Modifier = Modifier,
    modalSheetState: ModalSheetState,
    onDismiss : () -> Unit,
    content: @Composable ColumnScope.() -> Unit,
) {
    ModalSheet(
        modifier = modifier,
        sheetState = modalSheetState,
        onDismiss = onDismiss,
    ) {
        content()
    }
}