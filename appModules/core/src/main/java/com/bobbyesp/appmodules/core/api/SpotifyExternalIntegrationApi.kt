package com.bobbyesp.appmodules.core.api

import com.bobbyesp.appmodules.core.objects.external.PersonalizedRecommendationsRequest
import com.bobbyesp.appmodules.core.objects.external.PersonalizedRecommendationsResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface SpotifyExternalIntegrationApi {
    @POST("v2/personalized-recommendations")
    suspend fun personalizedRecommendations(
        @Body body: PersonalizedRecommendationsRequest
    ): PersonalizedRecommendationsResponse
}