package com.bobbyesp.spotdl_android.data

/**
 * Callback for download progress to be used in other library processes.
 */
interface DownloadProgressCallback {
    fun onProgressUpdate(progress: Float, etaInSeconds: Long, line: String)
}