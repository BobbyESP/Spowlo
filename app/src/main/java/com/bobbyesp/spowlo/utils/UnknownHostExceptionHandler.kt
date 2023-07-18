package com.bobbyesp.spowlo.utils

import java.net.UnknownHostException

object UnknownHostExceptionHandler : Thread.UncaughtExceptionHandler {
    override fun uncaughtException(t: Thread, e: Throwable) {
        if (e is UnknownHostException) {
            // Handle UnknownHostException here (e.g., log the error or show a user-friendly message)
            System.err.println("UnknownHostException occurred: ${e.message}")
        } else {
            // For other types of exceptions, you can log or handle them differently if needed
            System.err.println("Uncaught exception occurred: ${e.message}")
        }

        // Perform any necessary cleanup or shutdown tasks here.

        // Optionally, you can re-throw the exception to terminate the application.
        // However, be careful when doing this as it may leave the application in an inconsistent state.
        // throw e
    }

    fun setDefaultHandler() {
        Thread.setDefaultUncaughtExceptionHandler(this)
    }
}
