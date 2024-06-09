package com.bobbyesp.ui.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable

/**
 * Allows to create a saveable with an initial value where updating the initial value will lead to updating the
 * state *even if* the composable gets restored from saveable later on.
 *
 * rememberSaveableWithInitialValue should be used in a composable, when you want to initialize a mutable state
 * (e.g. for holding the value of a textinput field) with an initial value AND need the user input to survive
 * configuration changes AND want to allow changes to the initial value while being on a later screen
 * (i.e. while this composable is not active).
 */
@Composable
fun <T : Any?> rememberSaveableWithVolatileInitialValue(
    initialValue: T?
): MutableState<T?> {
    return key(initialValue) {
        rememberSaveable {
            mutableStateOf(initialValue)
        }
    }
}

/**
 * Allows to create a saveable with an initial value where updating the initial value will lead to updating the
 * state *even if* the composable gets restored from saveable later on.
 *
 * rememberSaveableWithInitialValue should be used in a composable, when you want to initialize a mutable state
 * (e.g. for holding the value of a textinput field) with an initial value AND need the user input to survive
 * configuration changes AND want to allow changes to the initial value while being on a later screen
 * (i.e. while this composable is not active).
 */
@JvmName("rememberSaveableWithVolatileInitialValueNotNull")
@Composable
fun <T : Any> rememberSaveableWithVolatileInitialValue(
    initialValue: T
): MutableState<T> {
    return key(initialValue) {
        rememberSaveable {
            mutableStateOf(initialValue)
        }
    }
}