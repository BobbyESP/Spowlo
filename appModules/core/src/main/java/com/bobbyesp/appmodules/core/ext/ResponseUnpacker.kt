package com.bobbyesp.appmodules.core.ext

import com.google.protobuf.Any
import com.google.protobuf.Message

@Suppress("UNCHECKED_CAST")
fun Any.dynamicUnpack(): Message = unpack(Class.forName(typeUrl.split("/")[1].let {
    if (!it.startsWith("com.spotify")) "com.spotify.${it}" else it
}) as Class<out Message>)