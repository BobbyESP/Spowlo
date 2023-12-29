package com.bobbyesp.library.dto

import kotlinx.serialization.Serializable

@Serializable
data class Song(
    val name: String = "",
    val artists: List<String> = listOf(),
    val artist: String = "",
    val album_name: String = "",
    val album_artist: String = "",
    val genres: List<String>? = listOf(),
    val disc_number: Int? = 0,
    val disc_count: Int? = 0,
    val duration: Double = 0.0,
    val year: Int = 0,
    val date: String = "",
    val track_number: Int? = 0,
    val tracks_count: Int? = 0,
    val song_id: String = "",
    val explicit: Boolean = false,
    val publisher: String? = "",
    val url: String = "",
    val isrc: String? = "",
    val cover_url: String = "",
    val copyright_text: String? = "",
    val download_url: String? = null,
    val song_list: SongList? = null,
    val list_position: Int? = null,
    val lyrics: String? = null,
    val album_id: String? = null,
)

@Serializable
data class SongList(
    val name: String = "",
    val url: String = "",
    val urls: List<String> = listOf(),
    val songs: List<Song> = listOf(),
    val genres: List<String>? = listOf(),
    val albums: List<String>? = listOf(),
    val artist: ArtistSongInfo = ArtistSongInfo(),
    val description: String = "",
    val author_url: String = "",
    val author_name: String = "",
    val cover_url: String = "",
)

@Serializable
data class ArtistSongInfo(
    val external_urls: Map<String, String> = emptyMap(),
    val href: String = "",
    val id: String = "",
    val name: String = "",
    val type: String = "",
    val uri: String = ""
)

//Q: How I can make this val nullable?  val genres: List<String>? = listOf(),
//A: You can use the nullable type List<String>? = listOf() or the non-nullable type List<String> = listOf() with a default value of null.

//Q: The parser tells me that the field is not nullable, but I know that it is. What should I do?
