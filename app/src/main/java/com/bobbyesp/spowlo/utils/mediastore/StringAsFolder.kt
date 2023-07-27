package com.bobbyesp.spowlo.utils.mediastore

import java.io.File

internal fun String.asFolder() = File(this).parentFile?.name.orEmpty()