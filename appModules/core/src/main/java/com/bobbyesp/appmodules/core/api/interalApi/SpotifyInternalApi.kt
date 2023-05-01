package com.bobbyesp.appmodules.core.api.interalApi

import com.bobbyesp.appmodules.core.objects.playlists.LikedSongsResponse
import com.bobbyesp.appmodules.core.objects.tags.ContentFilterResponse
import com.bobbyesp.appmodules.core.objects.ui_components.UiResponse
import com.bobbyesp.appmodules.core.utils.Log
import com.bobbyesp.appmodules.core.utils.SpotifyUtils
import com.bobbyesp.spowlo.proto.SearchViewResponse
import com.spotify.dac.api.v1.proto.DacRequest
import com.spotify.dac.api.v1.proto.DacResponse
import com.spotify.home.dac.viewservice.v1.proto.HomeViewServiceRequest
import com.spotify.playlist4.Playlist4ApiProto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import spotify.popcount2.proto.Popcount2External
import java.util.Locale
import java.util.TimeZone
import java.util.UUID

interface SpotifyInternalApi {
    @GET("/homeview/v1/home")
    suspend fun getHomeView(@Query("is_car_connected") carConnected: Boolean): UiResponse

    @GET("/chartview/v5/overview/android")
    suspend fun getChartView(): UiResponse

    @GET("/radio-apollo/v5/radio-hub")
    suspend fun getRadioHub(@Header("Accept-Language") language:String = Locale.getDefault().language): UiResponse

    @GET("/me/tracks")
    suspend fun getSavedTracks(): LikedSongsResponse

    @GET("/hubview-mobile-v1/browse/{id}")
    suspend fun getBrowseView(@Path("id") pageId: String = "", @Header("Accept-Language") language:String = Locale.getDefault().language): UiResponse

    @GET("/album-entity-view/v2/album/{id}")
    suspend fun getAlbumView(@Path("id") pageId: String, @Query("checkDeviceCapability") checkDeviceCapability: Boolean = true, @Header("Accept-Language") language:String = Locale.getDefault().language): UiResponse

    @GET("/artistview/v1/artist/{id}")
    suspend fun getArtistView(@Path("id") pageId: String, @Query("purchase_allowed") purchaseAllowed: Boolean = false, @Query("timeFormat") timeFormat: String = "24h", @Header("Accept-Language") language:String = Locale.getDefault().language): UiResponse

    @GET("/artistview/v1/artist/{id}/releases")
    suspend fun getReleasesView(@Path("id") pageId: String, @Query("checkDeviceCapability") checkDeviceCapability: Boolean = true, @Header("Accept-Language") language:String = Locale.getDefault().language): UiResponse

    @GET("/listening-history/v2/mobile/{timestamp}")
    suspend fun getListeningHistory(@Path("timestamp") timestamp: String = "", @Query("type") type: String = "merged", @Query("last_component_had_play_context") idk: Boolean = false, @Header("Accept-Language") language:String = Locale.getDefault().language): UiResponse

    @GET("/content-filter/v1/liked-songs")
    @Headers("Accept: application/json")
    suspend fun getCollectionTags(@Query("subjective") subjective: Boolean = true): ContentFilterResponse

    @POST("/home-dac-viewservice/v1/view")
    suspend fun getDacHome(@Body request: DacRequest = buildDacRequestForHome(), @Header("Accept-Language") acceptLanguage: String = Locale.getDefault().language): DacResponse

    @GET("/pam-view-service/v1/AllPlans")
    suspend fun getAllPlans(@Header("Accept-Language") language:String = Locale.getDefault().language): DacResponse

    @GET("/pam-view-service/v1/PlanOverview")
    suspend fun getPlanOverview(@Header("Accept-Language") language:String = Locale.getDefault().language): DacResponse

    @GET("/popcount/v2/playlist/{id}/count")
    suspend fun getPlaylistPopCount(@Path("id") id: String = ""): Popcount2External.PopcountResult

    @GET("/playlist/v2/user/{username}/rootlist")
    suspend fun getRootlist(
        @Path("username") username: String,
        @Query("decorate") decorate: String = "attributes,owner", // client: revision,attributes,length,owner,capabilities
        @Query("from") offset: Int = 0,
        @Query("length") size: Int = 120
    ): Playlist4ApiProto.SelectedListContent

    @GET("/playlist/v2/user/{username}/rootlist/diff")
    @Headers("x-accept-list-items: audio-track, audio-episode, video-episode")
    suspend fun getRootlistDelta(
        @Path("username") username: String,
        @Query("revision") revision: String,
        @Query("handlesContent") handles: String = "",
        @Query("hint_revision") targetRevision: String,
    ): Playlist4ApiProto.SelectedListContent

    @GET("/searchview/v3/search")
    suspend fun search(
        @Query("request_id") id: String = UUID.randomUUID().toString(),
        @Query("query") query: String,
        @Query("catalogue") catalogue: String = "premium",
        @Query("entity_types") types: String = "album,artist,genre,playlist,user_profile,track,audio_episode,show",
        @Query("timestamp") timestamp: Long = System.currentTimeMillis(),
        @Query("limit") limit: Int = 15,
        @Query("page_token") pageToken: String = ""
    ): SearchViewResponse

    companion object {
        fun buildDacRequestForHome (bFacet: String = "default") = DacRequest.newBuilder().apply {
            Log.d("buildDacRequestForHome", "Creating Dac request for home page // bFacet: $bFacet")
            uri = "dac:home" // dac:home-static
            featureRequest = com.google.protobuf.Any.pack(HomeViewServiceRequest.newBuilder().apply {
                facet = bFacet
                clientTimezone = TimeZone.getDefault().id
                putFeatureFlags("ic_flag_enabled", "true")
            }.build())
            clientInfo = DacRequest.ClientInfo.newBuilder().apply {
                appName = "ANDROID_MUSIC_APP"
                version = SpotifyUtils.SPOTIFY_APP_VERSION
            }.build()
        }.build()
    }
}
