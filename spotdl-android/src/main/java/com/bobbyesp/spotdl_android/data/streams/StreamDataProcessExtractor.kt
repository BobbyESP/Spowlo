package com.bobbyesp.spotdl_android.data.streams

import android.util.Log
import com.bobbyesp.spotdl_android.BuildConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.io.Reader
import java.nio.charset.StandardCharsets

internal class StreamDataProcessExtractor(
    private val buffer: StringBuffer,
    private val inputStream: InputStream,
    private val callback: ((Float, Long, String) -> Unit)?
) {

    /**
     * Starts the process of reading the InputStream and processing the output
     * @param scope The CoroutineScope to use for the process
     */
    suspend fun start(scope: CoroutineScope) {
        scope.launch {
            try {
                withContext(Dispatchers.IO) {
                    val input: Reader = InputStreamReader(inputStream, StandardCharsets.UTF_8)
                    val currentLine = StringBuilder()
                    var nextChar: Int
                    while (input.read().also { nextChar = it } != -1) {
                        buffer.append(nextChar.toChar())
                        if (nextChar == '\r'.code || nextChar == '\n'.code && callback != null) {
                            val line = currentLine.toString()
                            processOutputLine(line)
                            currentLine.setLength(0)
                            continue
                        }
                        currentLine.append(nextChar.toChar())
                    }
                }
            } catch (e: IOException) {
                if (BuildConfig.DEBUG) Log.e(TAG, "Failed trying to read the InputStream", e)
            }
        }
    }

    /**
     * Processes the output line
     * @param line The line to process
     */
    private suspend fun processOutputLine(line: String) {
        // callback?.let { it(getProgress(line), getEta(line), line) } TODO: implement
        callback?.let { it(0f, 0L, line) }
    }

    companion object {
        private const val TAG = "StreamDataProcess"
    }
}