package com.bobbyesp.appmodules.core

import android.content.Context
import com.bobbyesp.appmodules.core.ext.ConfigFilesDefs
import com.bobbyesp.appmodules.core.utils.Log
import com.bobbyesp.appmodules.core.utils.SpotifyUtils
import com.spotify.connectstate.Connect
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import xyz.gianlu.librespot.core.Session
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SpotifySessionManager @Inject constructor(
    @ApplicationContext val appContext: Context,
) {
    private var _session: Session? = null
    val session get() = _session ?: throw IllegalStateException("Session is not created yet!")

    fun createSession(): Session.Builder =
        Session.Builder(createCfg()).setDeviceType(Connect.DeviceType.SMARTPHONE).setDeviceName(
            SpotifyUtils.getDeviceName(appContext)
        ).setDeviceId(null).setPreferredLocale(Locale.getDefault().language)

    suspend fun awaitSession(): Session {
        while (_session == null) {
            withContext(Dispatchers.IO) {
                delay(100)
                Log.d("awaitSession()","Waiting for session...")
            }
        }
        return session
    }

    private fun createCfg() =
        Session.Configuration.Builder().setCacheEnabled(true).setDoCacheCleanUp(true)
            .setCacheDir(ConfigFilesDefs.getCacheDir(appContext))
            .setStoredCredentialsFile(ConfigFilesDefs.getCredentialsFile(appContext)).build()

    fun isSignedIn() = _session?.isValid ?: false
    fun setSession(s: Session) {
        _session = s
    }

    //session.cdn().streamExternalEpisode() lol for podcasts
}