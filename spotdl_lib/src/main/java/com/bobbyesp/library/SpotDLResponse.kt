package com.bobbyesp.library

open class SpotDLResponse(
    val commands: List<String>,
    val exitCode: Int,
    val elapsedTime: Long,
    val output: String,
    val error: String
)