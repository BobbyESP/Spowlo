package com.bobbyesp.ui.motion

/*
 * Copyright 2021 SOUP
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

object MotionConstants {
    const val DefaultMotionDuration: Int = 300
    const val DefaultFadeInDuration: Int = 150
    const val DefaultFadeOutDuration: Int = 75
    val DefaultSlideDistance: Dp = 30.dp

    const val DURATION = 600
    const val DURATION_ENTER = 400
    const val DURATION_ENTER_SHORT = 300
    const val DURATION_EXIT = 200
    const val DURATION_EXIT_SHORT = 100

    const val initialOffset = 0.10f
}