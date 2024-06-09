package com.bobbyesp.model

import android.net.Uri
import android.os.Parcelable
import androidx.compose.runtime.Stable
import kotlinx.parcelize.Parcelize
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
@Parcelize
@Stable
data class Song(
    val id: Long,
    val title: String,
    val artist: String,
    val album: String,
    @Serializable(with = UriSerializer::class) val artworkPath: Uri? = null,
    val duration: Double,
    val path: String,
    val fileName: String
) : Parcelable {
    companion object {
        val empty = Song(-1, "", "", "", null, 0.0, "", "")
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
