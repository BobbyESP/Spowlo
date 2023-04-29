package com.bobbyesp.appmodules.core.api

import com.bobbyesp.appmodules.core.objects.misc.SpBlendInviteLink
import retrofit2.http.POST

interface SpotifyBlendApi {
    @POST("/blend-invitation/v1/generate")
    suspend fun generateBlend(): SpBlendInviteLink
}