package com.bobbyesp.spowlo.util

import java.io.File

object FilesUtil {
    fun File.createEmptyFile(fileName: String) {
        kotlin.runCatching {
            this.mkdir()
            this.resolve(fileName).createNewFile()
        }.onFailure { it.printStackTrace() }
    }
}