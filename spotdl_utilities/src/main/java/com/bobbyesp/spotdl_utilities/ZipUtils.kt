package com.bobbyesp.spotdl_utilities

import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

object ZipUtils {
    fun decompressFile(zipFile: File, destDirectory: File) {
        val buffer = ByteArray(1024)

        ZipInputStream(FileInputStream(zipFile)).use { zipInputStream ->
            var zipEntry = zipInputStream.nextEntry
            while (zipEntry != null) {
                val entryPath = destDirectory.absolutePath + File.separator + zipEntry.name
                val entryFile = File(entryPath)

                if (zipEntry.isDirectory) {
                    entryFile.mkdirs()
                } else {
                    entryFile.parentFile?.mkdirs()
                    FileOutputStream(entryFile).use { fileOutputStream ->
                        var length: Int
                        while (zipInputStream.read(buffer).also { length = it } > 0) {
                            fileOutputStream.write(buffer, 0, length)
                        }
                    }
                }

                zipEntry = zipInputStream.nextEntry
            }
        }
    }

    fun decompressFiles(zipFile: File, destDirectory: File, onProgress: (Int) -> Unit) {
        val buffer = ByteArray(1024)

        ZipInputStream(FileInputStream(zipFile)).use { zipInputStream ->
            var zipEntry = zipInputStream.nextEntry
            var totalBytesRead = 0
            while (zipEntry != null) {
                val entryPath = destDirectory.absolutePath + File.separator + zipEntry.name
                val entryFile = File(entryPath)

                if (zipEntry.isDirectory) {
                    entryFile.mkdirs()
                } else {
                    entryFile.parentFile?.mkdirs()
                    FileOutputStream(entryFile).use { fileOutputStream ->
                        var length: Int
                        while (zipInputStream.read(buffer).also { length = it } > 0) {
                            fileOutputStream.write(buffer, 0, length)
                            totalBytesRead += length
                            onProgress(totalBytesRead)
                        }
                    }
                }

                zipEntry = zipInputStream.nextEntry
            }
        }
    }

    fun compressFiles(zipFile: File, files: Array<File>) {
        val buffer = ByteArray(1024)

        ZipOutputStream(FileOutputStream(zipFile)).use { zipOutputStream ->
            for (file in files) {
                FileInputStream(file).use { fileInputStream ->
                    val zipEntry = ZipEntry(file.name)
                    zipOutputStream.putNextEntry(zipEntry)

                    var length: Int
                    while (fileInputStream.read(buffer).also { length = it } > 0) {
                        zipOutputStream.write(buffer, 0, length)
                    }

                    zipOutputStream.closeEntry()
                }
            }
        }
    }

    fun compressFiles(zipFile: File, files: Array<File>, onProgress: (Int) -> Unit) {
        val buffer = ByteArray(1024)

        ZipOutputStream(FileOutputStream(zipFile)).use { zipOutputStream ->
            for (file in files) {
                FileInputStream(file).use { fileInputStream ->
                    val zipEntry = ZipEntry(file.name)
                    zipOutputStream.putNextEntry(zipEntry)

                    var length: Int
                    while (fileInputStream.read(buffer).also { length = it } > 0) {
                        zipOutputStream.write(buffer, 0, length)
                        onProgress(length)
                    }

                    zipOutputStream.closeEntry()
                }
            }
        }
    }
}