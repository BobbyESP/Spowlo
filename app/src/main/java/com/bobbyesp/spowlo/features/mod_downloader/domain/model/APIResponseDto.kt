package com.bobbyesp.spowlo.features.mod_downloader.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class APIResponseDto(
    val Latest_Versions: LatestVersionResponseDto = LatestVersionResponseDto(),
    val apps: AppsResponseDto = AppsResponseDto(),
)

@Serializable
data class ApkResponseDto(
    val version: String = "Unknown",
    val link: String = "",
)

@Serializable
data class AppsResponseDto(
    val Regular: List<ApkResponseDto> = emptyList(),
    val Regular_Cloned: List<ApkResponseDto> = emptyList(),
    val AMOLED: List<ApkResponseDto> = emptyList(),
    val AMOLED_Cloned: List<ApkResponseDto> = emptyList(),
    val Lite: List<ApkResponseDto> = emptyList(),
)

@Serializable
data class LatestVersionResponseDto(
    val Regular: String = "Unknown",
    val Regular_Cloned: String = "Unknown",
    val AMOLED: String = "Unknown",
    val AMOLED_Cloned: String = "Unknown",
    val Lite: String = "Unknown",
)