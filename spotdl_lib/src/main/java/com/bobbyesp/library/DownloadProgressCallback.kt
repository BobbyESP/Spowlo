package com.bobbyesp.library

interface DownloadProgressCallback {
    fun onProgressUpdate(progress: Float, etaInSeconds: Long, line: String)
}