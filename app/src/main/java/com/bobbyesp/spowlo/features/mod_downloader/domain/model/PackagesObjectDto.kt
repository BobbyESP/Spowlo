package com.bobbyesp.spowlo.features.mod_downloader.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class PackagesObjectDto(
    val Title: String = "",
    val Link: String = "",
    val Mirror: String = ""
)