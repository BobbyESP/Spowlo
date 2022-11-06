package com.bobbyesp.spowlo.domain.spotify.model

import com.google.gson.annotations.SerializedName

data class PackagesObject(
    @SerializedName("Title")
    val Title: String,
    @SerializedName("Link")
    val Link: String
)
