package com.bobbyesp.appmodules.core.api

import com.bobbyesp.appmodules.core.objects.graphql.ExtractedColors
import com.bobbyesp.appmodules.core.objects.graphql.GqlWrap
import retrofit2.http.GET
import retrofit2.http.Query

interface SpotifyPartnersApi {
    @GET("/pathfinder/v1/query")
    suspend fun fetchExtractedColors(
        @Query("operationName") opName: String = "fetchExtractedColors",
        @Query("extensions") extensions: String = "{\"persistedQuery\":{\"version\":1,\"sha256Hash\":\"d7696dd106f3c84a1f3ca37225a1de292e66a2d5aced37a66632585eeb3bbbfa\"}}",
        @Query("variables") variables: String // variables={"uris":["{picUrl}"]}
    ): GqlWrap<ExtractedColors>
}