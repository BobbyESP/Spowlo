package com.bobbyesp.appmodules.core.objects.external

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class PersonalizedRecommendationsRequest(
    @Json(name = "external_accessory_description") val accessory: PersonalizedRecommendationsAccessory,
    @Json(name = "contextual_signals") val signals: List<String>,
    @Json(name = "client_date_time") val dateTime: String, // ISO time
)

@JsonClass(generateAdapter = true)
class PersonalizedRecommendationsAccessory(
    val integration: String,
    @Json(name = "client_id") val clientId: String,
    val name: String,
    @Json(name = "transport_type") val transportType: String,
    val category: String,
    val company: String,
    val model: String,
    val version: String,
    val protocol: String,
    @Json(name = "sender_id") val senderId: String,
) {
    companion object {
        val Auto = PersonalizedRecommendationsAccessory(
            integration = "android_auto",
            transportType = "bluetooth_or_usb",
            category = "car",
            protocol = "media_session",
            senderId = "com.google.android.projection.gearhead",
            clientId = "",
            name = "",
            company = "",
            model = "",
            version = ""
        )
    }
}

@JsonClass(generateAdapter = true)
class PersonalizedRecommendationsResponse(
    @Json(name = "section_content") val content: List<PersonalizedRecommendationsSection>
)

@JsonClass(generateAdapter = true)
class PersonalizedRecommendationsSection(
    val name: String,
    val title: String,
    val uri: String,
    @Json(name = "section_items") val items: List<PersonalizedRecommendationsItem>
)

@JsonClass(generateAdapter = true)
class PersonalizedRecommendationsItem(
    val title: String,
    val subtitle: String?,
    val uri: String,
    @Json(name = "image_url") val image: String?
)