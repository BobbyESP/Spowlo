package com.bobbyesp.spowlo.ui.player

import android.content.Context
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class PlayerAsBottomSheetViewModel @Inject constructor(
    @ApplicationContext context: Context,
): ViewModel() {
    private val mutablePageViewState = MutableStateFlow(PageViewState())
    val pageViewState = mutablePageViewState.asStateFlow()
    data class PageViewState(
        val playing : Boolean = false,
    )
}