package com.bobbyesp.appmodules.hub.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bobbyesp.appmodules.core.objects.ui_components.HubText
import com.bobbyesp.appmodules.hub.ui.LocalHubScreenDelegate

@Composable
fun SectionHeaderComponent(
    text: HubText,
) {
    Box(
        Modifier
            .padding(
                vertical = 8.dp,
                horizontal = if (LocalHubScreenDelegate.current.isSurroundedWithPadding()) 0.dp else 16.dp
            )
    ) {
        Text(
            text = text.title!!,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            modifier = Modifier.align(Alignment.CenterStart)
        )
    }
}