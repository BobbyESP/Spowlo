package com.bobbyesp.spowlo.utils.files

import java.io.File

object FilesUtil {
    fun readFile(file: File): String {
        return file.readText()
    }
}