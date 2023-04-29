package com.bobbyesp.appmodules.core.api

import com.spotify.lyrics.v2.lyrics.proto.ColorLyricsResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface SpotifyColorsLyricsApi {
    @GET("/color-lyrics/v2/track/{spotifyId}/")
    suspend fun getLyrics(
        @Path("spotifyId") spotifyId: String,
        @Query("vocalRemoval") vocalRemoval: Boolean = false,
        @Query("syllableSync") syllableSync: Boolean = false,
        @Query("clientLanguage") clientLanguage: String = "en_US",
    ): ColorLyricsResponse
}