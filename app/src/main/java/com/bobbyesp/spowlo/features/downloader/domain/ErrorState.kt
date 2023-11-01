package com.bobbyesp.spowlo.features.downloader.domain

import com.bobbyesp.spowlo.R

data class ErrorState(
    val errorReport: String = "",
    val errorMessageResId: Int = R.string.unknown_error,
) {
    fun isErrorOccurred(): Boolean =
        errorMessageResId != R.string.unknown_error || errorReport.isNotEmpty()
}
