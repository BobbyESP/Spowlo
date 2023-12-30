package com.bobbyesp.ui.ext

import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.ui.unit.dp

fun CornerBasedShape.top() = copy(bottomStart = CornerSize(0.dp), bottomEnd = CornerSize(0.dp))