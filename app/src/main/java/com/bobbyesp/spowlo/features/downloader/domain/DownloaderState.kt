package com.bobbyesp.spowlo.features.downloader.domain

sealed class DownloaderState {
    data object Downloading : DownloaderState()
    data object FetchingInfo : DownloaderState()
    data object Idle : DownloaderState()
}