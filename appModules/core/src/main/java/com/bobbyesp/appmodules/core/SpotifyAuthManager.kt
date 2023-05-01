package com.bobbyesp.appmodules.core

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import xyz.gianlu.librespot.core.Session
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SpotifyAuthManager @Inject constructor(
    private val spSessionManager: SpotifySessionManager,
    private val spPlayerManager: SpotifyPlayerManager,
) {
    suspend fun authWith(username: String, password: String) = withContext(Dispatchers.IO) {
        try {
            spSessionManager.setSession(
                spSessionManager.createSession().userPass(username, password).create()
            )
            //spPlayerManager.createPlayer()
            //spCollectionManager.init()
            AuthResult.Success
        } catch (se: Session.SpotifyAuthenticationException) {
            AuthResult.SpError(se.message ?: "Unknown error")
        } catch (e: Exception) {
            e.printStackTrace()
            AuthResult.Exception(e)
        }
    }

    suspend fun authStored() = withContext(Dispatchers.IO) {
        runCatching {
            spSessionManager.setSession(spSessionManager.createSession().stored().create())
            //spPlayerManager.createPlayer()
            //spCollectionManager.init()
        }
    }

    sealed class AuthResult {
        object Success : AuthResult()
        class SpError(val msg: String) : AuthResult()
        class Exception(val e: kotlin.Exception) : AuthResult()
    }

    fun reset() {
        File(spSessionManager.appContext.filesDir, "spa_creds").delete()
    }

}