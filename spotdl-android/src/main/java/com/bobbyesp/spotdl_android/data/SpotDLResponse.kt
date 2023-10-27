package com.bobbyesp.spotdl_android.data

class SpotDLResponse(
    val commands: List<String?>,
    val exitCode: Int,
    val elapsedTime: Long,
    val output: String,
    val error: String
) {
    val isSuccess: Boolean
        get() =
            exitCode == 0
}