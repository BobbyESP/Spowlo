package com.bobbyesp.spowlo.domain.spotify.model

import com.bobbyesp.spowlo.util.api.Status
import com.google.gson.annotations.SerializedName

data class APIResponse(
    @SerializedName("Regular_Latest")
    val Regular_Latest: String,
    @SerializedName("Amoled_Latest")
    val Amoled_Latest: String,
    @SerializedName("RC_Latest")
    val RC_Latest: String,
    @SerializedName("ABC_Latest")
    val ABC_Latest: String,
    @SerializedName("Regular")
    val Regular: List<PackagesObject>,
    @SerializedName("Amoled")
    val Amoled: List<PackagesObject>,
    @SerializedName("Regular_Cloned")
    val Regular_Cloned: List<PackagesObject>,
    @SerializedName("Amoled_Cloned")
    val Amoled_Cloned: List<PackagesObject>
) {
    var status: Status = Status.SUCCESS
    var message: String? = null
}