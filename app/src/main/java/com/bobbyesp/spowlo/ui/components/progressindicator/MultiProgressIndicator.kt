package com.bobbyesp.spowlo.ui.components.progressindicator

import androidx.compose.foundation.Canvas
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp
import com.bobbyesp.spowlo.ui.theme.blendWith
import com.bobbyesp.spowlo.ui.theme.compositeSurfaceElevation
import com.bobbyesp.spowlo.utils.storage.StorageUtil

@Composable
fun DeviceStorageSizesBar(
    modifier: Modifier,
    storageSize: StorageUtil.StorageSize,
    application: Long,
) {
    val appBackground = MaterialTheme.colorScheme.primary
    val bgTotal = MaterialTheme.colorScheme.compositeSurfaceElevation(4.dp)
    val bgOthers = MaterialTheme.colorScheme.compositeSurfaceElevation(8.dp).blendWith(appBackground, 0.2f)

    Canvas(modifier) {
        val width = size.width
        val height = size.height

        val total = storageSize.total
        val others = storageSize.taken

        // Calculate the percentage of the total width that each bar should take up
        val othersPercent = others / total.toFloat()
        val appPercent = application / total.toFloat()
        val othersOffsetEnd = width * othersPercent
        val appOffsetEnd = othersOffsetEnd + (width * appPercent)

        // OTHERS - APPLICATION
        drawLine(bgTotal, Offset(0f, 0f), Offset(width, 0f), height, StrokeCap.Round)
        drawLine(bgOthers, Offset(0f, 0f), Offset(othersOffsetEnd, 0f), height, StrokeCap.Round)
        drawLine(appBackground, Offset(othersOffsetEnd, 0f), Offset(appOffsetEnd, 0f), height, StrokeCap.Round)
    }
}