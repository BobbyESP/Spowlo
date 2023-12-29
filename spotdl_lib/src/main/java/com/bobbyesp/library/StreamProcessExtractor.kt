package com.bobbyesp.library

import android.util.Log
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.io.Reader
import java.nio.charset.StandardCharsets
import java.util.regex.Matcher
import java.util.regex.Pattern

internal class StreamProcessExtractor(
    private val buffer: StringBuffer,
    private val stream: InputStream,
    private val callback: ((Float, Long, String) -> Unit)? = null
) : Thread() {

    private val cleanOutRegex: Pattern =
        Pattern.compile("(\\x1B[@-Z\\\\-_]|[\\x80-\\x9A\\x9C-\\x9F]|(?:\\x1B\\[|\\x9B)[0-?]*[ -/]*[@-~])")
    init {
        start()
    }

    //Based on this output that changes every second: Alan Walker - Shut Up                    Converting         ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━╺━━━━━━━━━━━━━━━  66% 0:00:02
    //NF - The Search                          Converting         ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━╸━━━━━━━━━━  78% 0:00:01
    //do the next:
    //1. Get the percentage
    //2. Get the ETA
    //3. Get the song name
    //4. Get the current status

    override fun run() {
        try {
            //Read the stream and get the output line by line on real time
            val reader: Reader = InputStreamReader(stream, StandardCharsets.UTF_8)
            val bufferedReader = BufferedReader(reader)
            var line: String?
            val arrayOfLines: MutableList<String> = mutableListOf()
            while (bufferedReader.readLine().also { line = it } != null) {
                //Just read the line, cut that line in it's end and add it to the buffer.
                // Then, the buffer will be read by the UI and after that, it will be cleared

                val readLine = line as CharSequence

                //clean output
                val matcher: Matcher = cleanOutRegex.matcher(readLine)
                val cleanLine = matcher.replaceAll("")
                if(cleanLine != "") processOutputLine(cleanLine)
                arrayOfLines.add(cleanLine)
                buffer.setLength(0)
                continue
            }

            //Make appear all the lines in the stdOut of the Logcat
            //delete from the array the empty lines
            arrayOfLines.removeAll { it == "" }
            buffer.append(arrayOfLines.joinToString("\n"))

        } catch (e: IOException) {
            if (BuildConfig.DEBUG) Log.e(TAG, "Failed to read stream", e)
        }
    }



    private fun processOutputLine(line: String) {
        //if is debug, print the line
        if (BuildConfig.DEBUG) Log.d(TAG, line)
        callback?.let { it(getProgress(line), getEta(line), line) }
    }

    private fun getProgress(line: String): Float{
        //Get the two numbers before an % in the line
        val regex = Regex("(\\d+)%")
        val matchResult = regex.find(line)
        //Log the result
        if (BuildConfig.DEBUG) Log.d(TAG, "Progress: ${matchResult?.groupValues?.get(1)?.toFloat() ?: 0f}")
        PERCENT = matchResult?.groupValues?.get(1)?.toFloat() ?: 0f
        //divide percent by 100 to get a value between 0 and 1
        return PERCENT / 100f
    }

    private fun getEta(line: String): Long{
        //Get the estimated time from the numbers with this format "00:00:00
        val regex = Regex("(\\d+:\\d+:\\d+)")
        val matchResult = regex.find(line)
        //Separate the result by 3 groups; hours, minutes and seconds
        val hours = matchResult?.groupValues?.get(1)?.split(":")?.get(0)?.toInt() ?: 0
        val minutes = matchResult?.groupValues?.get(1)?.split(":")?.get(1)?.toInt() ?: 0
        val seconds = matchResult?.groupValues?.get(1)?.split(":")?.get(2)?.toInt() ?: 0
        //Log the result
        if (BuildConfig.DEBUG) Log.d(TAG, "ETA: $hours:$minutes:$seconds")
        //Convert the time to seconds
        ETA = (hours * 3600 + minutes * 60 + seconds).toLong()
        return ETA

    }

    companion object{
        private val TAG = StreamProcessExtractor::class.java.simpleName

        private var ETA: Long = -1
        private var PERCENT = -1.0f
    }

}