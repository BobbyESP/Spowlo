package com.bobbyesp.spowlo.utils

import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Environment
import android.provider.DocumentsContract
import android.util.Log
import android.webkit.MimeTypeMap
import androidx.annotation.CheckResult
import androidx.core.content.FileProvider
import androidx.documentfile.provider.DocumentFile
import com.bobbyesp.spowlo.App.Companion.context
import com.bobbyesp.spowlo.R
import com.bobbyesp.spowlo.ui.pages.history.THUMBNAIL_REGEX
import okhttp3.internal.closeQuietly
import java.io.File
import java.io.InputStream


//////////////////////////////////////////////////////////////
// THANKS TO THE SEAL APP FOR THE FOLLOWING CODE SNIPPETS :)//
//            https://github.com/JunkFood02/Seal            //
//////////////////////////////////////////////////////////////

const val AUDIO_REGEX = "(mp3|aac|opus|m4a)$"

object FilesUtil {
    fun readFile(file: File): String {
        return file.readText()
    }

    fun openFileFromResult(downloadResult: Result<List<String>>) {
        val filePaths = downloadResult.getOrNull()
        if (filePaths.isNullOrEmpty()) return
        openFile(filePaths.first()) {
            ToastUtil.makeToastSuspend(context.getString(R.string.file_unavailable))
        }
    }

    inline fun openFile(path: String, onFailureCallback: (Throwable) -> Unit) =
        path.runCatching {
            createIntentForOpeningFile(this)?.run { context.startActivity(this) }
                ?: throw Exception()
        }.onFailure {
            onFailureCallback(it)
        }

    private fun createIntentForFile(path: String?): Intent? {
        if (path == null) return null

        val uri = path.runCatching {
            DocumentFile.fromSingleUri(context, Uri.parse(path)).run {
                if (this?.exists() == true) {
                    this.uri
                } else if (File(this@runCatching).exists()) {
                    FileProvider.getUriForFile(
                        context,
                        context.getFileProvider(),
                        File(this@runCatching)
                    )
                } else null
            }
        }.getOrNull() ?: return null

        return Intent().apply {
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            data = uri
        }
    }

    fun createIntentForOpeningFile(path: String?): Intent? = createIntentForFile(path)?.let {
        it.apply {
            action = (Intent.ACTION_VIEW)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
    }


    fun createIntentForSharingFile(path: String?): Intent? = createIntentForFile(path)?.apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_STREAM, data)
        val mimeType = data?.let { context.contentResolver.getType(it) } ?: "media/*"
        setDataAndType(this.data, mimeType)
        clipData = ClipData(
            null,
            arrayOf(mimeType),
            ClipData.Item(data)
        )
    }

    fun Context.getFileProvider() = "$packageName.provider"

    fun createIntentForShareAudioFile(path: String?): Intent? = createIntentForFile(path)?.apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_STREAM, this.data)
        type = "audio/*"
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        clipData = ClipData(
            null,
            arrayOf("audio/*"),
            ClipData.Item(data)
        )
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }

    fun String.getFileSize(): Long = this.run {
        val length = File(this).length()
        if (length == 0L)
            DocumentFile.fromSingleUri(context, Uri.parse(this))?.length() ?: 0L
        else length
    }

    fun deleteFile(path: String) =
        path.runCatching {
            if (!File(path).delete())
                DocumentFile.fromSingleUri(context, Uri.parse(this))?.delete()
        }

    @CheckResult
    fun scanFileToMediaLibraryPostDownload(title: String, downloadDir: String): List<String> =
        File(downloadDir)
            .walkTopDown()
            //THE TITLE IS THE [xx67238xx] THAT SEAL USES TO IDENTIFY THE FILE.
            .filter { it.isFile && it.path.contains(title) }
            .map { it.absolutePath }
            .toMutableList()
            .apply {
                MediaScannerConnection.scanFile(
                    context, this.toList().toTypedArray(),
                    null, null
                )
                removeAll { it.contains(Regex(THUMBNAIL_REGEX)) }
            }


    fun scanDownloadDirectoryToMediaLibrary(downloadDir: String) =
        File(downloadDir).walkTopDown().filter { it.isFile }.map { it.absolutePath }.run {
            MediaScannerConnection.scanFile(
                context, this.toList().toTypedArray(),
                null, null
            )
        }

    @CheckResult
    fun moveFilesToSdcard(
        tempPath: File,
        sdcardUri: String
    ): List<String> {
        val uriList = mutableListOf<String>()
        val destDir = Uri.parse(sdcardUri).run {
            DocumentsContract.buildDocumentUriUsingTree(
                this,
                DocumentsContract.getTreeDocumentId(this)
            )
        }
        tempPath.runCatching {
            walkTopDown().forEach {
                if (it.isDirectory) return@forEach
                val mimeType =
                    MimeTypeMap.getSingleton().getMimeTypeFromExtension(it.extension) ?: "*/*"

                val destUri = DocumentsContract.createDocument(
                    context.contentResolver,
                    destDir,
                    mimeType,
                    it.name
                ) ?: return@forEach

                val inputStream = it.inputStream()
                val outputStream =
                    context.contentResolver.openOutputStream(destUri) ?: return@forEach
                inputStream.copyTo(outputStream)
                inputStream.closeQuietly()
                outputStream.closeQuietly()
                uriList.add(destUri.toString())
            }
        }.onFailure {
            it.printStackTrace()
        }

        tempPath.deleteRecursively()
        return uriList
    }

    fun clearTempFiles(downloadDir: File): Int {
        var count = 0
        downloadDir.walkTopDown().forEach {
            if (it.isFile) {
                if (it.delete())
                    count++
            }
        }
        return count
    }

    fun Context.getConfigDirectory(): File = cacheDir

    fun Context.getConfigFile(suffix: String = "") =
        File(getConfigDirectory(), "config$suffix.txt")

    fun Context.getCookiesFile() =
        File(getConfigDirectory(), "cookies.txt")

    fun Context.getTempDir() = File(filesDir, "tmp")

    fun Context.getSdcardTempDir(child: String?): File = File(filesDir, "sdcard_tmp").run {
        child?.let { resolve(it) } ?: this
    }

    fun File.createEmptyFile(fileName: String) = this.runCatching {
        mkdir()
        resolve(fileName).createNewFile()
    }.onFailure { it.printStackTrace() }


    fun writeContentToFile(content: String, file: File): File {
        file.writeText(content)
        return file
    }

    fun getRealPath(treeUri: Uri): String {
        val path: String = treeUri.path.toString()
        Log.d(TAG, path)
        if (!path.contains("primary:")) {
            ToastUtil.makeToast("This directory is not supported")
            return File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).absolutePath,
                "Spowlo"
            ).absolutePath
        }
        val last: String = path.split("primary:").last()
        return "/storage/emulated/0/$last"
    }

    fun inputStreamToString(inputStream: InputStream): String {
        val bytes = ByteArray(inputStream.available())
        inputStream.read(bytes, 0, bytes.size)
        return String(bytes)
    }


    private const val TAG = "FileUtil"

}