package com.bobbyesp.spowlo.ui.player

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.bobbyesp.spowlo.ui.components.bottomsheets.BottomSheetState
import com.bobbyesp.spowlo.ui.components.bottomsheets.NavBarBottomSheet

@Composable
fun PlayerAsBottomSheet(
    state: BottomSheetState,
    navController: NavController,
    modifier: Modifier = Modifier,
) {
    NavBarBottomSheet(
        state = state,
        modifier = modifier,
        backgroundColor = MaterialTheme.colorScheme.surfaceColorAtElevation(NavigationBarDefaults.Elevation),
        onDismiss = {
                    
        },
        collapsedContent = {
            Text(text = "Test lmao")
        }
    ) {
        Text(text = "Test lmao2")
    }
}