package com.bobbyesp.appmodules.core.utils

import android.util.Base64
import com.google.protobuf.ByteString
import java.nio.ByteBuffer

object Revision {
  fun base64ToRevision(b64: String) = Base64.decode(b64, Base64.DEFAULT).let { b64dec ->
    val buffer = ByteBuffer.wrap(b64dec)

    val revId = buffer.int
    val revHash = ByteArray(buffer.remaining()).also { buffer.get(it) }.joinToString("") {
      (0xFF and it.toInt()).toString(16).padStart(2, '0')
    }.padEnd(40, '0')

    return@let "$revId,$revHash"
  }

  fun byteStringToRevision(bs: ByteString): String {
    val buffer = bs.asReadOnlyByteBuffer()

    val revId = buffer.int
    val revHash = ByteArray(buffer.remaining()).also { buffer.get(it) }.joinToString("") {
      (0xFF and it.toInt()).toString(16).padStart(2, '0')
    }.padEnd(40, '0')

    return "$revId,$revHash"
  }
}