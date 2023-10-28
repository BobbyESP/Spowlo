package com.bobbyesp.commonutilities.utils

import android.system.ErrnoException
import android.system.Os
import org.apache.commons.compress.archivers.zip.ZipFile
import org.apache.commons.io.IOUtils
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.nio.charset.StandardCharsets

object ZipUtilities {
    @Throws(IOException::class, ErrnoException::class, IllegalAccessException::class)
    fun unzip(sourceFile: File?, targetDirectory: File) {
        ZipFile(sourceFile).use { zipFile ->
            val entries = zipFile.entries
            while (entries.hasMoreElements()) {
                val entry = entries.nextElement()
                val entryDestination = File(targetDirectory, entry.name)
                // prevent zipSlip
                if (!entryDestination.canonicalPath
                        .startsWith(targetDirectory.canonicalPath + File.separator)
                ) {
                    throw IllegalAccessException("Entry is outside of the target dir: " + entry.name)
                }
                if (entry.isDirectory) {
                    entryDestination.mkdirs()
                } else if (entry.isUnixSymlink) {
                    zipFile.getInputStream(entry).use { `in` ->
                        val symlink = IOUtils.toString(
                            `in`,
                            StandardCharsets.UTF_8
                        )
                        Os.symlink(symlink, entryDestination.absolutePath)
                    }
                } else {
                    entryDestination.parentFile?.mkdirs()
                    zipFile.getInputStream(entry).use { `in` ->
                        FileOutputStream(entryDestination).use { out ->
                            IOUtils.copy(
                                `in`,
                                out
                            )
                        }
                    }
                }
            }
        }
    }
}