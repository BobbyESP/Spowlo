package com.bobbyesp.spowlo.features.downloader

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import android.webkit.CookieManager
import com.bobbyesp.spowlo.ui.ext.toDomain
import com.bobbyesp.spowlo.utils.connectWithDelimiter

object Cookies {
    private const val COOKIE_HEADER =
        "# Netscape HTTP Cookie File\n" + "# Auto-generated by Spowlo built-in WebView\n"
    data class Cookie(
        val domain: String = "",
        val name: String = "",
        val value: String = "",
        val includeSubdomains: Boolean = true,
        val path: String = "/",
        val secure: Boolean = true,
        val expiry: Long = 0L,
    ) {
        constructor(
            url: String,
            name: String,
            value: String
        ) : this(domain = url.toDomain(), name = name, value = value)

        fun toNetscapeCookieString(): String {
            return connectWithDelimiter(
                domain,
                includeSubdomains.toString().uppercase(),
                path,
                secure.toString().uppercase(),
                expiry.toString(),
                name,
                value,
                delimiter = "\u0009"
            )
        }
    }
    object Scheme {
        const val NAME = "name"
        const val VALUE = "value"
        const val SECURE = "is_secure"
        const val EXPIRY = "expires_utc"
        const val HOST = "host_key"
        const val PATH = "path"
    }

    fun getCookiesContentFromDatabase(context: Context): Result<String> = runCatching {
        CookieManager.getInstance().run {
            if (!hasCookies()) throw Exception("There is no cookies in the database!")
            flush()
        }
        SQLiteDatabase.openDatabase(
            context.dataDir.absolutePath + "/app_webview/Default/Cookies",  // -> "/data/data/com.bobbyesp.spowlo/app_webview/Default/Cookies"
            null,
            SQLiteDatabase.OPEN_READONLY
        ).run {
            val projection = arrayOf(
                Scheme.HOST,
                Scheme.EXPIRY,
                Scheme.PATH,
                Scheme.NAME,
                Scheme.VALUE,
                Scheme.SECURE
            )
            val cookieList = mutableListOf<Cookie>()
            query(
                "cookies", projection, null, null, null, null, null
            ).run {
                while (moveToNext()) {
                    val expiry = getLong(getColumnIndexOrThrow(Scheme.EXPIRY))
                    val name = getString(getColumnIndexOrThrow(Scheme.NAME))
                    val value = getString(getColumnIndexOrThrow(Scheme.VALUE))
                    val path = getString(getColumnIndexOrThrow(Scheme.PATH))
                    val secure = getLong(getColumnIndexOrThrow(Scheme.SECURE)) == 1L
                    val hostKey = getString(getColumnIndexOrThrow(Scheme.HOST))

                    val host = if (hostKey[0] != '.') ".$hostKey" else hostKey
                    cookieList.add(
                        Cookie(
                            domain = host,
                            name = name,
                            value = value,
                            path = path,
                            secure = secure,
                            expiry = expiry
                        )
                    )
                }
                close()
            }
            Log.d("Cookies", "Loaded ${cookieList.size} cookies from database!")
            cookieList.fold(StringBuilder(COOKIE_HEADER)) { acc, cookie ->
                acc.append(cookie.toNetscapeCookieString()).append("\n")
            }.toString()
        }
    }
}