package com.bobbyesp.utilities.utilities

object URL {
    val urlRegex = Regex(
        "((http|https)://)?(www.)?" + "[a-zA-Z0-9@:%._\\+~#?&//=]" + "{2,256}\\.[a-z]" + "{2,6}\\b([-a-zA-Z0-9@:%" + "._\\+~#?&//=]*)"
    )

    fun getUrls(text: String): List<String> {
        return urlRegex.findAll(text).map { it.value }.toList()
    }
}