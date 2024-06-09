package com.bobbyesp.ext

import com.bobbyesp.utilities.mediastore.AudioFileMetadata

typealias PropertyMap = HashMap<String, Array<String>>

fun PropertyMap.toAudioFileMetadata(separator: String = ", "): AudioFileMetadata {
    return AudioFileMetadata(
        title = this["TITLE"]?.getOrNull(0),
        artist = this["ARTIST"]?.joinOrNullToString(separator),
        album = this["ALBUM"]?.getOrNull(0),
        albumArtist = this["ALBUMARTIST"]?.joinOrNullToString(separator),
        trackNumber = this["TRACKNUMBER"]?.getOrNull(0),
        discNumber = this["DISCNUMBER"]?.getOrNull(0),
        date = this["DATE"]?.getOrNull(0),
        genre = this["GENRE"]?.joinOrNullToString(separator),
        composer = this["COMPOSER"]?.joinOrNullToString(separator),
        lyricist = this["LYRICIST"]?.joinOrNullToString(separator),
        performer = this["PERFORMER"]?.joinOrNullToString(separator),
        conductor = this["CONDUCTOR"]?.joinOrNullToString(separator),
        remixer = this["REMIXER"]?.joinOrNullToString(separator),
        comment = this["COMMENT"]?.getOrNull(0),
    )
}

fun PropertyMap.toModifiableMap(separator: String = ", "): MutableMap<String, String?> {
    return mutableMapOf(
        "TITLE" to this["TITLE"]?.getOrNull(0),
        "ARTIST" to this["ARTIST"]?.joinOrNullToString(separator),
        "ALBUM" to this["ALBUM"]?.getOrNull(0),
        "ALBUMARTIST" to this["ALBUMARTIST"]?.joinOrNullToString(separator),
        "TRACKNUMBER" to this["TRACKNUMBER"]?.getOrNull(0),
        "DISCNUMBER" to this["DISCNUMBER"]?.getOrNull(0),
        "DATE" to this["DATE"]?.getOrNull(0),
        "GENRE" to this["GENRE"]?.joinOrNullToString(separator),
        "COMPOSER" to this["COMPOSER"]?.joinOrNullToString(separator),
        "LYRICIST" to this["LYRICIST"]?.joinOrNullToString(separator),
        "PERFORMER" to this["PERFORMER"]?.joinOrNullToString(separator),
        "CONDUCTOR" to this["CONDUCTOR"]?.joinOrNullToString(separator),
        "REMIXER" to this["REMIXER"]?.joinOrNullToString(separator),
        "COMMENT" to this["COMMENT"]?.getOrNull(0),
    )
}