package com.bobbyesp.spowlo.utils.mediastore

import com.kyant.taglib.PropertyMap
import kotlinx.serialization.Serializable

@Serializable
data class FileMetadata(
    // Basic tags
    val title: Array<String>? = null,
    val album: Array<String>? = null,
    val artist: Array<String>? = null,
    val albumArtist: Array<String>? = null,
    val subtitle: Array<String>? = null,
    val trackNumber: Array<String>? = null,
    val discNumber: Array<String>? = null,
    val date: Array<String>? = null,
    val originalDate: Array<String>? = null,
    val genre: Array<String>? = null,
    val comment: Array<String>? = null,

    // Sort names
    val titleSort: Array<String>? = null,
    val albumSort: Array<String>? = null,
    val artistSort: Array<String>? = null,
    val albumArtistSort: Array<String>? = null,
    val composerSort: Array<String>? = null,

    // Credits
    val composer: Array<String>? = null,
    val lyricist: Array<String>? = null,
    val conductor: Array<String>? = null,
    val remixer: Array<String>? = null,
    val performer: Array<String>? = null,

    // Other tags
    val isrc: Array<String>? = null,
    val asin: Array<String>? = null,
    val bpm: Array<String>? = null,
    val encodedBy: Array<String>? = null,
    val mood: Array<String>? = null,
    val media: Array<String>? = null,
    val label: Array<String>? = null,
    val catalogNumber: Array<String>? = null,
    val barcode: Array<String>? = null,
    val releaseCountry: Array<String>? = null,
    val releaseStatus: Array<String>? = null,
    val releaseType: Array<String>? = null
) {
    companion object {
        fun PropertyMap.toFileMetadata(): FileMetadata {
            return FileMetadata(
                title = this["TITLE"],
                album = this["ALBUM"],
                artist = this["ARTIST"],
                albumArtist = this["ALBUMARTIST"],
                subtitle = this["SUBTITLE"],
                trackNumber = this["TRACKNUMBER"],
                discNumber = this["DISCNUMBER"],
                date = this["DATE"],
                originalDate = this["ORIGINALDATE"],
                genre = this["GENRE"],
                comment = this["COMMENT"],
                titleSort = this["TITLESORT"],
                albumSort = this["ALBUMSORT"],
                artistSort = this["ARTISTSORT"],
                albumArtistSort = this["ALBUMARTISTSORT"],
                composerSort = this["COMPOSERSORT"],
                composer = this["COMPOSER"],
                lyricist = this["LYRICIST"],
                conductor = this["CONDUCTOR"],
                remixer = this["REMIXER"],
                performer = this["PERFORMER"],
                isrc = this["ISRC"],
                asin = this["ASIN"],
                bpm = this["BPM"],
                encodedBy = this["ENCODEDBY"],
                mood = this["MOOD"],
                media = this["MEDIA"],
                label = this["LABEL"],
                catalogNumber = this["CATALOGNUMBER"],
                barcode = this["BARCODE"],
                releaseCountry = this["RELEASECOUNTRY"],
                releaseStatus = this["RELEASESTATUS"],
                releaseType = this["RELEASETYPE"]
            )
        }
    }

    fun toPropertyMap(): PropertyMap {
        return mapOf(
            "TITLE" to title,
            "ALBUM" to album,
            "ARTIST" to artist,
            "ALBUMARTIST" to albumArtist,
            "SUBTITLE" to subtitle,
            "TRACKNUMBER" to trackNumber,
            "DISCNUMBER" to discNumber,
            "DATE" to date,
            "ORIGINALDATE" to originalDate,
            "GENRE" to genre,
            "COMMENT" to comment,
            "TITLESORT" to titleSort,
            "ALBUMSORT" to albumSort,
            "ARTISTSORT" to artistSort,
            "ALBUMARTISTSORT" to albumArtistSort,
            "COMPOSERSORT" to composerSort,
            "COMPOSER" to composer,
            "LYRICIST" to lyricist,
            "CONDUCTOR" to conductor,
            "REMIXER" to remixer,
            "PERFORMER" to performer,
            "ISRC" to isrc,
            "ASIN" to asin,
            "BPM" to bpm,
            "ENCODEDBY" to encodedBy,
            "MOOD" to mood,
            "MEDIA" to media,
            "LABEL" to label,
            "CATALOGNUMBER" to catalogNumber,
            "BARCODE" to barcode,
            "RELEASECOUNTRY" to releaseCountry,
            "RELEASESTATUS" to releaseStatus,
            "RELEASETYPE" to releaseType
        ).filterValues { it != null } as PropertyMap
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as FileMetadata

        if (!title.contentEquals(other.title)) return false
        if (!album.contentEquals(other.album)) return false
        if (!artist.contentEquals(other.artist)) return false
        if (!albumArtist.contentEquals(other.albumArtist)) return false
        if (!subtitle.contentEquals(other.subtitle)) return false
        if (!trackNumber.contentEquals(other.trackNumber)) return false
        if (!discNumber.contentEquals(other.discNumber)) return false
        if (!date.contentEquals(other.date)) return false
        if (!originalDate.contentEquals(other.originalDate)) return false
        if (!genre.contentEquals(other.genre)) return false
        if (!comment.contentEquals(other.comment)) return false
        if (!titleSort.contentEquals(other.titleSort)) return false
        if (!albumSort.contentEquals(other.albumSort)) return false
        if (!artistSort.contentEquals(other.artistSort)) return false
        if (!albumArtistSort.contentEquals(other.albumArtistSort)) return false
        if (!composerSort.contentEquals(other.composerSort)) return false
        if (!composer.contentEquals(other.composer)) return false
        if (!lyricist.contentEquals(other.lyricist)) return false
        if (!conductor.contentEquals(other.conductor)) return false
        if (!remixer.contentEquals(other.remixer)) return false
        if (!performer.contentEquals(other.performer)) return false
        if (!isrc.contentEquals(other.isrc)) return false
        if (!asin.contentEquals(other.asin)) return false
        if (!bpm.contentEquals(other.bpm)) return false
        if (!encodedBy.contentEquals(other.encodedBy)) return false
        if (!mood.contentEquals(other.mood)) return false
        if (!media.contentEquals(other.media)) return false
        if (!label.contentEquals(other.label)) return false
        if (!catalogNumber.contentEquals(other.catalogNumber)) return false
        if (!barcode.contentEquals(other.barcode)) return false
        if (!releaseCountry.contentEquals(other.releaseCountry)) return false
        if (!releaseStatus.contentEquals(other.releaseStatus)) return false
        if (!releaseType.contentEquals(other.releaseType)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = title?.contentHashCode() ?: 0
        result = 31 * result + album.hashCode()
        result = 31 * result + artist.hashCode()
        result = 31 * result + albumArtist.hashCode()
        result = 31 * result + subtitle.hashCode()
        result = 31 * result + trackNumber.hashCode()
        result = 31 * result + discNumber.hashCode()
        result = 31 * result + date.hashCode()
        result = 31 * result + originalDate.hashCode()
        result = 31 * result + genre.hashCode()
        result = 31 * result + comment.hashCode()
        result = 31 * result + titleSort.hashCode()
        result = 31 * result + albumSort.hashCode()
        result = 31 * result + artistSort.hashCode()
        result = 31 * result + albumArtistSort.hashCode()
        result = 31 * result + composerSort.hashCode()
        result = 31 * result + composer.hashCode()
        result = 31 * result + lyricist.hashCode()
        result = 31 * result + conductor.hashCode()
        result = 31 * result + remixer.hashCode()
        result = 31 * result + performer.hashCode()
        result = 31 * result + isrc.hashCode()
        result = 31 * result + asin.hashCode()
        result = 31 * result + bpm.hashCode()
        result = 31 * result + encodedBy.hashCode()
        result = 31 * result + mood.hashCode()
        result = 31 * result + media.hashCode()
        result = 31 * result + label.hashCode()
        result = 31 * result + catalogNumber.hashCode()
        result = 31 * result + barcode.hashCode()
        result = 31 * result + releaseCountry.hashCode()
        result = 31 * result + releaseStatus.hashCode()
        result = 31 * result + releaseType.hashCode()
        return result
    }
}
