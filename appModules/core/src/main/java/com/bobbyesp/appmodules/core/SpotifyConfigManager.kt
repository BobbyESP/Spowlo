package com.bobbyesp.appmodules.core

import android.content.Context
import androidx.datastore.core.CorruptionException
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.core.Serializer
import com.bobbyesp.spowlo.protos.AppConfig
import com.bobbyesp.spowlo.protos.AudioNormalization
import com.bobbyesp.spowlo.protos.AudioQuality
import com.bobbyesp.spowlo.protos.PlayerConfig
import com.google.protobuf.InvalidProtocolBufferException
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import java.io.File
import java.io.InputStream
import java.io.OutputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SpotifyConfigManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    val dataStore = DataStoreFactory.create(object: Serializer<AppConfig> {
        override val defaultValue = DEFAULT
        override suspend fun writeTo(t: AppConfig, output: OutputStream) = t.writeTo(output)

        override suspend fun readFrom(input: InputStream) = try {
            AppConfig.parseFrom(input)
        } catch (e: InvalidProtocolBufferException) {
            throw CorruptionException("Proto files failed to parse!")
        }
    }) { File(context.filesDir, "spa_prefs") }

    fun syncPlayerConfig(): PlayerConfig = runBlocking { dataStore.data.first().playerConfig }
    companion object {
        val EMPTY = object: DataStore<AppConfig> {
            override val data: Flow<AppConfig> get() = emptyFlow()
            override suspend fun updateData(transform: suspend (t: AppConfig) -> AppConfig) = TODO("This is an empty DataStore!")
        }

        val DEFAULT = AppConfig.newBuilder().apply {
            setPlayerConfig(PlayerConfig.newBuilder().apply {
                autoplay = true
                normalization = true
                preferredQuality = AudioQuality.HIGH
                normalizationLevel = AudioNormalization.BALANCED
                crossfade = 0
                preload = true
                useTremolo = false
            })
        }.build()
    }
}