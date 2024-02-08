package com.bobbyesp.spowlo.ui.pages.utilities.tag_editor.editor

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bobbyesp.spowlo.data.local.MediaStoreReceiver
import com.kyant.taglib.Metadata
import com.kyant.taglib.TagLib
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ID3MetadataEditorPageViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {
    private val mutablePageViewState = MutableStateFlow(PageViewState())
    val pageViewState = mutablePageViewState.asStateFlow()

    data class PageViewState(
        val state: ID3MetadataEditorPageState = ID3MetadataEditorPageState.Loading,
        val lyrics: String = ""
    )

    fun loadTrackMetadata(path: String, fileName: String) {
        updateState(ID3MetadataEditorPageState.Loading)

        try {
            MediaStoreReceiver.getFileDescriptorFromPath(context, path).let { songFd ->
                val metadata = TagLib.getMetadata(
                    songFd?.dup()?.detachFd() ?: throw Exception("File descriptor is null"),
                    fileName = fileName,
                    readLyrics = true
                )
                if (metadata == null) {
                    updateState(ID3MetadataEditorPageState.Error(Exception("Metadata is null")))
                    return
                }
                updateLyrics(metadata.propertyMap["LYRICS"]?.get(0) ?: "")
                updateState(ID3MetadataEditorPageState.Success(metadata))
            }
        } catch (e: Exception) {
            updateState(ID3MetadataEditorPageState.Error(e))
        }
    }

    private fun getSongEmbeddedLyrics(path: String, fileName: String) {
        try {
            val songFd = MediaStoreReceiver.getFileDescriptorFromPath(context, path)
                ?: throw Exception("File descriptor is null")
            val lyrics = TagLib.getLyrics(songFd.fd, path)

            if (lyrics.isNullOrEmpty()) {
                return
            } else {
                updateLyrics(lyrics)
            }
        } catch (e: Exception) {
            Log.i("Lyrics", e.toString())
        }
    }

    fun saveMetadata(newMetadata: Metadata, path: String, fileName: String): Boolean {
        try {
            MediaStoreReceiver.getFileDescriptorFromPath(context, path).let { songFd ->
                val fd = songFd?.dup()?.detachFd() ?: throw Exception("File descriptor is null")
                Log.i(
                    "ID3MetadataEditorPageViewModel",
                    "Metadata saved successfully. New metadata: $newMetadata"
                )
                return TagLib.savePropertyMap(
                    fd,
                    fileName = fileName,
                    propertyMap = newMetadata.propertyMap
                )
            }
        } catch (e: Exception) {
            Log.e(
                "ID3MetadataEditorPageViewModel",
                "Error while trying to save metadata: ${e.message}"
            )
            return false
        }
    }

    fun sameMetadata(oldMetadata: Metadata, newMetadata: Metadata): Boolean {
        return oldMetadata == newMetadata
    }

    private fun updateState(state: ID3MetadataEditorPageState) {
        viewModelScope.launch(Dispatchers.Main) {
            mutablePageViewState.update {
                it.copy(
                    state = state
                )
            }
        }
    }

    private fun updateLyrics(lyrics: String) {
        mutablePageViewState.update {
            it.copy(
                lyrics = lyrics
            )
        }
    }

    companion object {
        sealed class ID3MetadataEditorPageState {
            data object Loading : ID3MetadataEditorPageState()
            data class Success(val metadata: Metadata) : ID3MetadataEditorPageState()
            data class Error(val throwable: Throwable) : ID3MetadataEditorPageState()
        }
    }
}
