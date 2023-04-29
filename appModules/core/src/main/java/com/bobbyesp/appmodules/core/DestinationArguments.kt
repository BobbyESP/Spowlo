package com.bobbyesp.appmodules.core

import androidx.navigation.NamedNavArgument

@JvmInline
value class DestNode(val url: String) {
    internal companion object {
        val REGEX = "\\{(.+?)\\}".toRegex()
    }
}

fun DestNode.map(arguments: Map<String, Any>): String {
    return url.replace(DestNode.REGEX) { result ->
        arguments[result.groupValues[1]]!!.toString()
    }
}

fun DestNode.mapArgs(arguments: Map<NamedNavArgument, Any>): String {
    val argMap = arguments.mapKeys { it.key.name }

    return url.replace(DestNode.REGEX) { result ->
        argMap[result.groupValues[1]].toString()
    }
}