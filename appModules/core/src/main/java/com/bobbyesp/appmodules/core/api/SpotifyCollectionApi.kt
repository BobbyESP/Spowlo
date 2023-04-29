package com.bobbyesp.appmodules.core.api

import com.spotify.collection2.v2.proto.Collection2V2
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface SpotifyCollectionApi {
    @POST("write")
    @Headers("Accept: application/vnd.collection-v2.spotify.proto", "Content-Type: application/vnd.collection-v2.spotify.proto")
    suspend fun write(@Body data: Collection2V2.WriteRequest)

    @POST("delta")
    @Headers("Accept: application/vnd.collection-v2.spotify.proto", "Content-Type: application/vnd.collection-v2.spotify.proto")
    suspend fun delta(@Body data: Collection2V2.DeltaRequest): Collection2V2.DeltaResponse

    @POST("paging")
    @Headers("Accept: application/vnd.collection-v2.spotify.proto", "Content-Type: application/vnd.collection-v2.spotify.proto")
    suspend fun paging(@Body data: Collection2V2.PageRequest): Collection2V2.PageResponse
}