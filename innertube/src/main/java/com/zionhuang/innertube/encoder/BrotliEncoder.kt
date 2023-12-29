package com.zionhuang.innertube.encoder

import io.ktor.client.plugins.compression.ContentEncoder
import io.ktor.client.plugins.compression.ContentEncoding
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.jvm.javaio.toByteReadChannel
import io.ktor.utils.io.jvm.javaio.toInputStream
import kotlinx.coroutines.CoroutineScope
import org.brotli.dec.BrotliInputStream

object BrotliEncoder : ContentEncoder {
    override val name: String = "br"

    override fun CoroutineScope.decode(source: ByteReadChannel): ByteReadChannel =
        BrotliInputStream(source.toInputStream()).toByteReadChannel()

    override fun CoroutineScope.encode(source: ByteReadChannel): ByteReadChannel =
        throw UnsupportedOperationException("Encode not implemented by the library yet.")
}

fun ContentEncoding.Config.brotli(quality: Float? = null) {
    customEncoder(BrotliEncoder, quality)
}