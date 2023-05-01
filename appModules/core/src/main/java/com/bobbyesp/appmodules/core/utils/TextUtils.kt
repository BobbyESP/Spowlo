package com.bobbyesp.appmodules.core.utils

import java.util.regex.Pattern


private fun matchUrlFromString(s: String, isMatchingMultiLink: Boolean = false): String {
    val builder = StringBuilder()
    val pattern =
        Pattern.compile("(http|https)://[\\w\\-_]+(\\.[\\w\\-_]+)+([\\w\\-.,@?^=%&:/~+#]*[\\w\\-@?^=%&/~+#])?")
    with(pattern.matcher(s)) {
        if (isMatchingMultiLink)
            while (find()) {
                if (builder.isNotEmpty())
                    builder.append("\n")
                builder.append(group())
            }
        else if (find())
            builder.append(group())
    }
    return builder.toString()
}


fun connectWithDelimiter(vararg strings: String, delimiter: String = " · "): String {
    val builder = StringBuilder(strings.first())
    for (s in strings.asList().subList(1, strings.size)) {
        builder.append(delimiter)
        builder.append(s)
    }
    return builder.toString()
}

fun connectWithBlank(s1: String, s2: String): String {
    val f1 = s1.toEmpty()
    val f2 = s2.toEmpty()
    val blank = if (f1.isEmpty() || f2.isEmpty()) "" else " "
    return f1 + blank + f2
}

fun String.toEmpty() = if (equals("none") || equals("null")) "" else this