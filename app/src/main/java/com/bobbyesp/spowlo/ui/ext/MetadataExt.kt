package com.bobbyesp.spowlo.ui.ext

import com.kyant.tag.Metadata
import com.kyant.tag.Tags

fun Tags.toMetadata(oldMetadata: Metadata): Metadata {
    return Metadata(
        lengthInMilliseconds = oldMetadata.lengthInMilliseconds,
        bitrate = oldMetadata.bitrate,
        sampleRate = oldMetadata.sampleRate,
        channels = oldMetadata.channels,
        properties = this.toPropertiesMap()
    )
}
fun Tags.toPropertiesMap(): Map<String, Array<String>> {
    val properties = mutableMapOf<String, Array<String>>()

    properties["TITLE"] = title.toTypedArray()
    properties["ALBUM"] = album.toTypedArray()
    properties["ARTIST"] = artist.toTypedArray()
    properties["ALBUMARTIST"] = albumArtist.toTypedArray()
    properties["SUBTITLE"] = subtitle.toTypedArray()
    properties["TRACKNUMBER"] = trackNumber.toTypedArray()
    properties["DISCNUMBER"] = discNumber.toTypedArray()
    properties["DATE"] = date.toTypedArray()
    properties["ORIGINALDATE"] = originalDate.toTypedArray()
    properties["GENRE"] = genre.toTypedArray()
    properties["COMMENT"] = comment.toTypedArray()
    properties["TITLESORT"] = titleSort.toTypedArray()
    properties["ALBUMSORT"] = albumSort.toTypedArray()
    properties["ARTISTSORT"] = artistSort.toTypedArray()
    properties["ALBUMARTISTSORT"] = albumArtistSort.toTypedArray()
    properties["COMPOSERSORT"] = composerSort.toTypedArray()
    properties["COMPOSER"] = composer.toTypedArray()
    properties["LYRICIST"] = lyricist.toTypedArray()
    properties["CONDUCTOR"] = conductor.toTypedArray()
    properties["REMIXER"] = remixer.toTypedArray()
    properties["PERFORMER"] = performer.toTypedArray()
    properties["ISRC"] = isrc.toTypedArray()
    properties["ASIN"] = asin.toTypedArray()
    properties["BPM"] = bpm.toTypedArray()
    properties["ENCODEDBY"] = encodedBy.toTypedArray()
    properties["MOOD"] = mood.toTypedArray()
    properties["MEDIA"] = media.toTypedArray()
    properties["LABEL"] = label.toTypedArray()
    properties["CATALOGNUMBER"] = catalogNumber.toTypedArray()
    properties["BARCODE"] = barcode.toTypedArray()
    properties["RELEASECOUNTRY"] = releaseCountry.toTypedArray()
    properties["RELEASESTATUS"] = releaseStatus.toTypedArray()
    properties["RELEASETYPE"] = releaseType.toTypedArray()
    properties["MUSICBRAINZ_TRACKID"] = musicBrainzTrackId.toTypedArray()
    properties["MUSICBRAINZ_ALBUMID"] = musicBrainzAlbumId.toTypedArray()
    properties["MUSICBRAINZ_RELEASEGROUPID"] = musicBrainzReleaseGroupId.toTypedArray()
    properties["MUSICBRAINZ_RELEASETRACKID"] = musicBrainzReleaseTrackId.toTypedArray()
    properties["MUSICBRAINZ_WORKID"] = musicBrainzWorkId.toTypedArray()
    properties["MUSICBRAINZ_ARTISTID"] = musicBrainzArtistId.toTypedArray()
    properties["MUSICBRAINZ_ALBUMARTISTID"] = musicBrainzAlbumArtistId.toTypedArray()
    properties["ACOUSTID_ID"] = acoustidId.toTypedArray()
    properties["ACOUSTID_FINGERPRINT"] = acoustidFingerprint.toTypedArray()
    properties["MUSICIP_PUID"] = musicIpPuid.toTypedArray()

    return properties
}