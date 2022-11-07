package com.bobbyesp.spowlo.domain.spotify.model

data class APICallState(
    val isLoading: Boolean = false,
    val APIResponse: APIResponse? = null
)
