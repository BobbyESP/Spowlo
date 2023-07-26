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