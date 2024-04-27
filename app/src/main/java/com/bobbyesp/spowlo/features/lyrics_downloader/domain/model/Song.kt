package com.bobbyesp.spowlo.features.lyrics_downloader.domain.model

import android.net.Uri
import android.os.Parcelable
import androidx.compose.runtime.Immutable
import com.adamratzman.spotify.models.Track
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.Serializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable
@Immutable
data class Song(
    val id: Long,
    val title: String,
    val artist: String,
    val album: String,
    @Serializable(with = UriSerializer::class) val albumArtPath: Uri? = null,
    val duration: Double,
    val path: String,
    val fileName: String? = null
) : Parcelable {
    constructor(parcel: android.os.Parcel) : this(
        parcel.readLong(),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readParcelable(Uri::class.java.classLoader),
        parcel.readDouble(),
        parcel.readString()!!,
        parcel.readString()!!
    )

    override fun writeToParcel(parcel: android.os.Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeString(title)
        parcel.writeString(artist)
        parcel.writeString(album)
        parcel.writeParcelable(albumArtPath, flags)
        parcel.writeDouble(duration)
        parcel.writeString(path)
        parcel.writeString(fileName)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Song> {
        override fun createFromParcel(parcel: android.os.Parcel): Song {
            return Song(parcel)
        }

        override fun newArray(size: Int): Array<Song?> {
            return arrayOfNulls(size)
        }
    }
}

@OptIn(ExperimentalSerializationApi::class)
@Serializer(forClass = Uri::class)
object UriSerializer : KSerializer<Uri> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("Uri", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: Uri) {
        encoder.encodeString(value.toString())
    }

    override fun deserialize(decoder: Decoder): Uri {
        return Uri.parse(decoder.decodeString())
    }
}


fun List<Track>.toSongs(): List<Song> {
    val songs = mutableListOf<Song>()
    this.forEach {
        songs.add(
            Song(
                id = 0,
                title = it.name,
                artist = it.artists.joinToString(", ") { artist -> artist.name ?: "" },
                album = it.album.name,
                duration = it.durationMs.toDouble(),
                path = it.externalUrls.spotify!!,
                albumArtPath = it.album.images?.firstOrNull()?.url?.let { url -> Uri.parse(url) },
                fileName = ""
            )
        )
    }
    return songs
}

fun Track.toSong(): Song {
    return Song(
        id = 0,
        title = this.name,
        artist = this.artists.joinToString(", ") { artist -> artist.name ?: "" },
        album = this.album.name,
        duration = this.durationMs.toDouble(),
        path = this.externalUrls.spotify!!,
        albumArtPath = this.album.images?.firstOrNull()?.url?.let { url -> Uri.parse(url) },
        fileName = ""
    )
}

fun Song.isFromSpotify(): Boolean {
    return this.path.contains("spotify")
}
