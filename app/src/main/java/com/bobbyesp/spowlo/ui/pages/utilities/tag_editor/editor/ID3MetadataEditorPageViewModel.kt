package com.bobbyesp.spowlo.ui.pages.utilities.tag_editor.editor

import android.app.RecoverableSecurityException
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModel
import com.bobbyesp.spowlo.MainActivity
import com.bobbyesp.spowlo.data.local.MediaStoreReceiver
import com.kyant.taglib.Metadata
import com.kyant.taglib.TagLib
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.io.IOException
import javax.inject.Inject

const val ON_WRITE_DATA_REQUEST_CODE = 1
@HiltViewModel
class ID3MetadataEditorPageViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {
    private val mutablePageViewState = MutableStateFlow(PageViewState())
    val pageViewState = mutablePageViewState.asStateFlow()

    data class PageViewState(
        val metadata: Metadata? = null,
        val state: ID3MetadataEditorPageState = ID3MetadataEditorPageState.Loading,
        val lyrics: String = ""
    )

    fun loadTrackMetadata(path: String, fileName: String) {
        updateState(ID3MetadataEditorPageState.Loading)

        try {
            MediaStoreReceiver.getFileDescriptorFromPath(context, path, mode = "r")?.use { songFd ->
                val fd = songFd.dup()?.detachFd() ?: throw IOException("File descriptor is null")
                val metadata = TagLib.getMetadata(
                    fd,
                    fileName = fileName,
                    readLyrics = true
                )
                if (metadata == null) {
                    updateState(ID3MetadataEditorPageState.Error(Exception("Metadata is null")))
                    return
                }
                val lyrics = metadata.propertyMap["LYRICS"]?.get(0) ?: ""
                updateLyrics(lyrics)
                updateMetadata(metadata)
                updateState(ID3MetadataEditorPageState.Success(metadata))
            }
        } catch (e: IOException) {
            Log.e(
                "ID3MetadataEditorPageViewModel",
                "Error while trying to load metadata: ${e.message}"
            )
            updateState(ID3MetadataEditorPageState.Error(e))
        }
    }

    fun saveMetadata(
        context: Context = this.context, // Added missing context parameter
        newMetadata: Metadata,
        path: String,
        fileName: String
    ): Boolean {
        return try {
            MediaStoreReceiver.getFileDescriptorFromPath(context, path, mode = "w")
                ?.let { fileDescriptor ->
                    val fd = fileDescriptor.dup()?.detachFd()
                        ?: throw IOException("File descriptor is null") // IOException instead of generic Exception
                TagLib.savePropertyMap(
                    fd,
                    fileName = fileName,
                    propertyMap = newMetadata.propertyMap
                )
                updateState(ID3MetadataEditorPageState.Success(newMetadata)) // Update state on success
                true
            } ?: false
        } catch (securityException: SecurityException) {
            Log.i("ID3MetadataEditorPageViewModel", "Security exception caught")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val recoverableSecurityException =
                    securityException as? RecoverableSecurityException
                        ?: throw RuntimeException(securityException.message, securityException)

                val intentSender =
                    recoverableSecurityException.userAction.actionIntent.intentSender
                ActivityCompat.startIntentSenderForResult(
                    MainActivity.getActivity(), intentSender, ON_WRITE_DATA_REQUEST_CODE,
                    null, 0, 0, 0, null
                )
            } else {
                throw RuntimeException(securityException.message, securityException)
            }
            false
        } catch (e: IOException) { // Catching IOException specifically
            Log.e(
                "ID3MetadataEditorPageViewModel",
                "Error while trying to save metadata: ${e.message}"
            )
            updateState(ID3MetadataEditorPageState.Error(e)) // Update state on error
            false
        }
    }


    private fun updateState(state: ID3MetadataEditorPageState) {
        mutablePageViewState.update {
            it.copy(
                state = state
            )
        }
    }

    private fun updateLyrics(lyrics: String) {
        mutablePageViewState.update {
            it.copy(
                lyrics = lyrics
            )
        }
    }

    private fun updateMetadata(metadata: Metadata? = null) {
        mutablePageViewState.update {
            it.copy(
                metadata = metadata
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