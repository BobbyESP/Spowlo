package com.bobbyesp.piped_lib.domain.model.stream

import kotlinx.serialization.Serializable

@Serializable
data class Chapter(
    val title: String,
    val image: String,
    val start: Int,
)
