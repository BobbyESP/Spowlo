package com.bobbyesp.spowlo.features.spotify.auth

import android.content.Context
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import com.adamratzman.spotify.SpotifyScope
import com.adamratzman.spotify.getSpotifyPkceAuthorizationUrl
import com.adamratzman.spotify.getSpotifyPkceCodeChallenge
import com.bobbyesp.spowlo.App.Companion.json
import com.bobbyesp.spowlo.BuildConfig
import kotlinx.serialization.encodeToString
import kotlin.random.Random

fun launchSpotifyAuth(context: Context, url: String) {
    val uri = Uri.parse(url)
    val customTabsIntent = CustomTabsIntent.Builder().build()
    customTabsIntent.launchUrl(context, uri)
}

fun generatePkceCodeVerifier(): String = (44..96).joinToString("") {
    (('a'..'z') + ('A'..'Z') + ('0'..'9')).random().toString()
}
fun generateStateString(): String = Random.nextLong().toString()
fun createCodeChallenge(pkceCodeVerifier: String) = getSpotifyPkceCodeChallenge(pkceCodeVerifier)
fun generateAuthUrl(codeChallenge: String, state: String) = getSpotifyPkceAuthorizationUrl(
    clientId = BuildConfig.CLIENT_ID,
    redirectUri = BuildConfig.SPOTIFY_REDIRECT_URI_PKCE,
    codeChallenge = codeChallenge,
    state = state,
    scopes = SpotifyScope.entries.toTypedArray()
)

object SpotifyAuthenticationHelpers {
    fun writeVariablesToFile(context: Context, variables: AuthenticationVariables) {
        //Use kotlinx serialization and the create a file in the cache dir of the app
        val file = context.cacheDir.resolve("auth_variables.json")
        if (!file.exists()) {
            file.createNewFile()
        }
        val json = json.encodeToString<AuthenticationVariables>(variables)
        file.writeText(json)
    }
    fun readVariablesFromFile(context: Context): AuthenticationVariables? {
        val file = context.cacheDir.resolve("auth_variables.json")
        if (!file.exists()) {
            return null
        }
        val fileInformation = file.readText()
        return json.decodeFromString<AuthenticationVariables>(fileInformation)
    }

    fun deleteVariablesFile(context: Context) {
        val file = context.cacheDir.resolve("auth_variables.json")
        if (file.exists()) {
            file.delete()
        }
    }
}
