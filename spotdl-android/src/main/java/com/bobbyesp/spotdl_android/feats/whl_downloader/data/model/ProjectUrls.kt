package com.bobbyesp.spotdl_android.feats.whl_downloader.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ProjectUrls(
    @SerialName("Documentation") val documentation: String,
    @SerialName("Homepage") val homepage: String,
    @SerialName("Repository") val repository: String
)

