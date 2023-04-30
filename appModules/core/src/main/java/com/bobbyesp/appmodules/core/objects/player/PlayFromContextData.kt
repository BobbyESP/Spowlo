package com.bobbyesp.appmodules.core.objects.player

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class PlayFromContextData (
    val uri: String,
    val player: PlayFromContextPlayerData,
)

@JsonClass(generateAdapter = true)
class PlayFromContextPlayerData (
    val context: PfcContextData? = null,
    val options: PfcOptions? = null,
    val state: PfcState? = null,
    val play_origin: Map<String, String> = mapOf(),
)

@JsonClass(generateAdapter = true)
class PfcContextData (
    val url: String? = null,
    val uri: String,
    val metadata: PfcContextMetadata? = null
)

@JsonClass(generateAdapter = true)
class PfcOptions (
    val skip_to: PfcOptSkipTo? = null,
    val player_options_override: PfcStateOptions? = null
)

@JsonClass(generateAdapter = true)
class PfcContextMetadata (
    val context_description: String? = null,
)

@JsonClass(generateAdapter = true)
class PfcOptSkipTo (
    val page_index: Int? = null,
    val track_uri: String
)

@JsonClass(generateAdapter = true)
class PfcState (
    val options: PfcStateOptions? = null
)

@JsonClass(generateAdapter = true)
class PfcStateOptions (
    val shuffling_context: Boolean = false
)