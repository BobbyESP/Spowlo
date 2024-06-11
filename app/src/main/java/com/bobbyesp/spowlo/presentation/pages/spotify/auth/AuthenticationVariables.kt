package com.bobbyesp.spowlo.presentation.pages.spotify.auth

import kotlinx.serialization.Serializable
import kotlin.time.Duration.Companion.minutes

@Serializable
data class AuthenticationVariables(
    val codeVerifier: String,
    val codeChallenge: String,
    val state: String,
    val authorizationUrl: String,
    val modificationTime: Long = System.currentTimeMillis()
) {
    //10 minutes from creation
    val isExpired: Boolean
        get() = System.currentTimeMillis() - modificationTime > 10.minutes.inWholeMilliseconds
}