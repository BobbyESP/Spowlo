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

internal class StreamFlowStorer(private val buffer: StringBuffer, private val stream: InputStream) {
    private val TAG = StreamFlowStorer::class.java.simpleName

    fun startStoring(scope: CoroutineScope) {
        scope.launch(Dispatchers.IO) {
            try {
                val reader: Reader = InputStreamReader(stream, StandardCharsets.UTF_8)
                var nextChar: Int
                while (reader.read().also { nextChar = it } != -1) {
                    withContext(Dispatchers.Main) {
                        buffer.append(nextChar.toChar())
                    }
                }
            } catch (e: IOException) {
                if (BuildConfig.DEBUG) Log.e(TAG, "Failed trying to read the InputStream", e)
            }
        }
    }
}
