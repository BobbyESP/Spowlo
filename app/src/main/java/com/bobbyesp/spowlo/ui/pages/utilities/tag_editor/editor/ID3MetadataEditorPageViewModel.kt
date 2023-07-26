package com.bobbyesp.spowlo.ui.pages.utilities.tag_editor.editor

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import com.kyant.tag.Metadata
import com.kyant.tag.Metadata.Companion.getLyrics
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ID3MetadataEditorPageViewModel @Inject constructor(
    @ApplicationContext context: Context
) : ViewModel() {
    private val mutablePageViewState = MutableStateFlow(PageViewState())
    val pageViewState = mutablePageViewState.asStateFlow()

    data class PageViewState(
        val state: ID3MetadataEditorPageState = ID3MetadataEditorPageState.Loading,
        val lyrics: String = ""
    )

    suspend fun loadTrackMetadata(path: String) {
        updateState(ID3MetadataEditorPageState.Loading)

        val metadata: Metadata?

        try {
            metadata = withContext(Dispatchers.IO) {
                Metadata.getMetadata(path)
            }

            Log.i("Metadata", metadata.toString())

            if (metadata == null) {
                updateState(ID3MetadataEditorPageState.Error(Exception("Metadata is null")))
                return
            }

            withContext(Dispatchers.IO) {
                getSongEmbeddedLyrics(path)
            }

            updateState(ID3MetadataEditorPageState.Success(metadata))
        } catch (e: Exception) {
            updateState(ID3MetadataEditorPageState.Error(e))
        }
    }


    private suspend fun getSongEmbeddedLyrics(path: String) {
        try {
            val lyrics = withContext(Dispatchers.IO) {
                getLyrics(path)
            }

            if (lyrics.isNullOrEmpty()) {
                return
            } else {
                mutablePageViewState.update {
                    PageViewState(
                        lyrics = lyrics
                    )
                }
            }
        } catch (e: Exception) {
            Log.i("Lyrics", e.toString())
        }
    }

    suspend fun saveMetadata(newMetadata: Metadata, path: String) {

    }

    fun sameMetadata(oldMetadata: Metadata, newMetadata: Metadata): Boolean {
        return oldMetadata == newMetadata
    }

    private fun updateState(state: ID3MetadataEditorPageState) {
        mutablePageViewState.update {
            PageViewState(
                state = state
            )
        }
    }
}

sealed class ID3MetadataEditorPageState {
    object Loading : ID3MetadataEditorPageState()
    data class Success(val metadata: Metadata) : ID3MetadataEditorPageState()
    data class Error(val throwable: Throwable) : ID3MetadataEditorPageState()
}