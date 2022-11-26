package com.bobbyesp.spowlo.util

import android.content.Context
import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.list.listItems
import com.anggrayudi.storage.SimpleStorageHelper
import com.anggrayudi.storage.extension.isDocumentsDocument
import com.anggrayudi.storage.extension.isDownloadsDocument
import com.anggrayudi.storage.extension.isTreeDocumentFile
import com.anggrayudi.storage.file.DocumentFileCompat
import com.anggrayudi.storage.file.PublicDirectory
import com.anggrayudi.storage.file.StorageId
import com.anggrayudi.storage.file.getAbsolutePath
import com.bobbyesp.spowlo.Spowlo.Companion.context
import com.bobbyesp.spowlo.presentation.MainActivity

object FileUtil {
    private val storageHelper = SimpleStorageHelper(MainActivity())

    private val storageIds = DocumentFileCompat.getStorageIds(context)

    fun checkStorageAccess(storageID: String): Boolean {
        return storageHelper.storage.isStorageAccessGranted(storageId = storageID)
    }

    //get storage ID
    fun getStorageID(): String {
        return "PRIMARY"
    }

    fun requestFullStorageAccess() {
        if(checkStorageAccess(getStorageID())) {
            return
        } else {
            storageHelper.storage.requestStorageAccess()
        }
    }

    fun requestStorageAccessToFolder(){
        storageHelper.requestStorageAccess(1)
    }

    fun setupSimpleStorage(savedInstanceState: Bundle?) {
        savedInstanceState?.let { storageHelper.onRestoreInstanceState(it) }
        storageHelper.onStorageAccessGranted = { _, root ->
            Toast.makeText(context, "Storage access granted to ${root.getAbsolutePath(context)}", Toast.LENGTH_SHORT).show()
        }
    }

    fun getListOfGrantedUris(): List<String> {
        val grantedUris = context.contentResolver.persistedUriPermissions
            .filter { it.isReadPermission && it.isWritePermission && it.uri.isTreeDocumentFile }
            .map {
                if (it.uri.isDownloadsDocument) {
                    if (getStorageID() == StorageId.PRIMARY) PublicDirectory.DOWNLOADS.absolutePath else ""
                } else if (it.uri.isDocumentsDocument) {
                    if (getStorageID() == StorageId.PRIMARY) PublicDirectory.DOCUMENTS.absolutePath else ""
                } else {
                    val uriPath = it.uri.path!! // e.g. /tree/primary:Music
                    val storageId = uriPath.substringBefore(':').substringAfterLast('/')
                    if (getStorageID() == storageId) {
                        val rootFolder = uriPath.substringAfter(':', "")
                        if (storageId == StorageId.PRIMARY) {
                            "${Environment.getExternalStorageDirectory()}/$rootFolder"
                        } else {
                            "/storage/$storageId/$rootFolder"
                        }
                    } else ""
                }
            }
            .filter { it.isNotEmpty() }
        return grantedUris
    }
}