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
import javax.inject.Inject

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
            MediaStoreReceiver.getFileDescriptorFromPath(context, path, mode = "r").use { songFd ->
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
                updateMetadata(metadata)
                updateState(ID3MetadataEditorPageState.Success(metadata))
            }
        } catch (e: Exception) {
            Log.e(
                "ID3MetadataEditorPageViewModel",
                "Error while trying to load metadata: ${e.message}"
            )
            updateState(ID3MetadataEditorPageState.Error(e))
        }
    }

    private var permissionRequestCount = 0
    private val maxPermissionRequests = 3 // Set a limit for permission requests

    fun saveMetadata(
        newMetadata: Metadata,
        path: String,
        fileName: String,
        callback: ((() -> Unit) -> Unit)? = null
    ): Boolean {
        return try {
            MediaStoreReceiver.getFileDescriptorFromPath(context, path, mode = "w")?.let { songFd ->
                val fd = songFd.dup()?.detachFd() ?: throw Exception("File descriptor is null")
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
                    MainActivity.getActivity(), intentSender, 1,
                    null, 0, 0, 0, null
                )

                if (permissionRequestCount < maxPermissionRequests) {
                    callback?.invoke {
                        saveMetadata(newMetadata, path, fileName, callback)
                    }
                    permissionRequestCount++
                } else {
                    Log.i("ID3MetadataEditorPageViewModel", "Permission request limit reached")
                }
            } else {
                throw RuntimeException(securityException.message, securityException)
            }
            false
        } catch (e: Exception) {
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
