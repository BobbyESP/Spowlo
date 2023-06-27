package com.bobbyesp.spotdl_android.data

/**
 * Exception thrown when there is an error with the SpotDL process.
 */
class SpotDLException : Exception {
    constructor(message: String?, cause: Throwable?) : super(message, cause)
    constructor(message: String?) : super(message)
    constructor(cause: Throwable?) : super(cause)
}
/**
 * Exception thrown when the SpotDL process is canceled.
 */
class CanceledException : Exception()

