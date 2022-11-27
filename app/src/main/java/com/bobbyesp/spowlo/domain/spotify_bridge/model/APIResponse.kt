package com.bobbyesp.spowlo.domain.spotify_bridge.model

import com.bobbyesp.spowlo.util.api.Status
import com.google.gson.annotations.SerializedName

data class APIResponse(
    @SerializedName("result")
    val result: String?,
) {
    var status: Status = Status.SUCCESS
    var message: String? = null
}
