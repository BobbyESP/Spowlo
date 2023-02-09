package com.bobbyesp.spowlo.features.mod_downloader.domain.model

import com.bobbyesp.spowlo.features.mod_downloader.util.Status
import kotlinx.serialization.Serializable

@Serializable
data class PackagesResponseDto(
    val Regular_Latest: String = "Unknown",
    val Amoled_Latest: String = "Unknown",
    val RC_Latest: String = "Unknown",
    val ABC_Latest: String = "Unknown",
    val Lite_Latest: String = "Unknown",
    // START OF NON USED FIELDS
    val App_Changelogs: String = "",
    val Supporters: String = "",
    val Rewarded_Ads: String = "",
    val Update: String = "",
    val Server: String = "",
    //FINISH OF NON USED FIELDS
    val Regular: List<PackagesObjectDto> = emptyList(),
    val Amoled: List<PackagesObjectDto> = emptyList(),
    val Regular_Cloned: List<PackagesObjectDto> = emptyList(),
    val Amoled_Cloned: List<PackagesObjectDto> = emptyList(),
    val Lite: List<PackagesObjectDto> = emptyList(),
    val Mod_Changelogs: List<ModChangelogsDto> = emptyList(),
)
@Serializable
data class ModChangelogsDto(
    val Mod_Changelogs: String = ""
)