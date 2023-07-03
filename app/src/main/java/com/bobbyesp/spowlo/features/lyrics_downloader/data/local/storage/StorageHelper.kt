package com.bobbyesp.spowlo.features.lyrics_downloader.data.local.storage

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lyrics
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.documentfile.provider.DocumentFile
import com.bobbyesp.spowlo.R
import com.bobbyesp.spowlo.features.lyrics_downloader.data.local.model.Song
import com.bobbyesp.spowlo.ui.components.cards.CardListItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

object StorageHelper {
    @Composable
    fun SaveLyricsButton(
        modifier: Modifier = Modifier,
        song: Song,
        lyrics: String,
    ) {
        val context = LocalContext.current
        val scope = rememberCoroutineScope()

        var fileUri: Uri?

        val launcher = rememberLauncherForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val intent = result.data?.data
                if (intent != null) {
                    fileUri = intent
                    scope.launch {
                        saveAndWriteLrcFile(
                            context, song.title, song.artist, lyrics, fileUri!!
                        )
                    }

                }
            }
        }

        val fileName = "${song.title} - ${song.artist}.lrc"

        // Create an Intent to open the SAF file picker
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "text/plain" // Set the MIME type to plain text
            putExtra(Intent.EXTRA_TITLE, fileName)
        }

        CardListItem(
            modifier = modifier,
            leadingContentIcon = Icons.Default.Lyrics,
            headlineContentText = stringResource(id = R.string.save_lyrics)
        ) {
            scope.launch(Dispatchers.IO) {
                launcher.launch(intent)
            }
        }
    }

    // Function to save and write the .lrc file using SAF
    private suspend fun saveAndWriteLrcFile(
        context: Context, songName: String, artistName: String, lyrics: String, uri: Uri?
    ) {
        withContext(Dispatchers.IO) {
            if (uri != null) {
                val documentFile = DocumentFile.fromSingleUri(context, uri)
                if (documentFile != null && documentFile.canWrite()) {
                    val outputStream = context.contentResolver.openOutputStream(uri)
                    outputStream?.bufferedWriter().use { writer ->
                        // Write the .lrc content to the file
                        writer?.write("[ti:$songName]\n")
                        writer?.write("[ar:$artistName]\n")
                        writer?.write(lyrics)
                    }
                    outputStream?.close()
                }
            }
        }
    }
}