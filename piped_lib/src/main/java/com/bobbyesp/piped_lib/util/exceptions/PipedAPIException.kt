package com.bobbyesp.piped_lib.util.exceptions

class PipedAPIException(
    val code: Int? = null,
    override val message: String,
    val error: String,
): Exception()