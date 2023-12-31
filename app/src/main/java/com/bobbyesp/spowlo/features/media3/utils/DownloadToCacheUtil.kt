@file:OptIn(UnstableApi::class)

package com.bobbyesp.spowlo.features.media3.utils

import android.content.Context
import androidx.annotation.OptIn
import androidx.core.net.toUri
import androidx.media3.common.PlaybackException
import androidx.media3.common.util.UnstableApi
import androidx.media3.database.DatabaseProvider
import androidx.media3.datasource.ResolvingDataSource
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.datasource.cache.SimpleCache
import androidx.media3.datasource.okhttp.OkHttpDataSource
import androidx.media3.exoplayer.offline.Download
import androidx.media3.exoplayer.offline.DownloadManager
import androidx.media3.exoplayer.offline.DownloadNotificationHelper
import com.bobbyesp.spowlo.App.Companion.applicationScope
import com.bobbyesp.spowlo.App.Companion.connectivityManager
import com.bobbyesp.spowlo.db.SpowloMusicDatabase
import com.bobbyesp.spowlo.db.entity.format.FormatEntity
import com.bobbyesp.spowlo.features.media3.data.services.ExoDownloadService
import com.bobbyesp.utilities.audio.model.AudioQuality
import com.bobbyesp.utilities.utilities.preferences.DefaultMaxParallelDownloadsExo
import com.bobbyesp.utilities.utilities.preferences.Preferences
import com.bobbyesp.utilities.utilities.preferences.Preferences.getInt
import com.bobbyesp.utilities.utilities.preferences.PreferencesKeys.AUDIO_QUALITY
import com.bobbyesp.utilities.utilities.preferences.PreferencesKeys.MAX_PARALLEL_DOWNLOADS_EXO
import com.zionhuang.innertube.YouTube
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import java.util.concurrent.Executor
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DownloadUtil @Inject constructor(
    @ApplicationContext context: Context,
    @DownloadCache val downloadCache: SimpleCache,
    @PlayerCache val playerCache: SimpleCache,
    databaseProvider: DatabaseProvider,
    database: SpowloMusicDatabase,
) {
    private val formatDao = database.formatDao()
    private val audioQuality =
        Preferences.EnumPrefs.getValue<AudioQuality>(AUDIO_QUALITY, AudioQuality.AUTO)
    private val songUrlCache = HashMap<String, Pair<String, Long>>()
    private val dataSourceFactory = ResolvingDataSource.Factory(
        CacheDataSource.Factory().setCache(playerCache).setUpstreamDataSourceFactory(
            OkHttpDataSource.Factory(
                OkHttpClient.Builder().proxy(YouTube.proxy).build()
            )
        )
    ) { dataSpec ->
        val mediaId = dataSpec.key ?: error("No media id")
        val length = if (dataSpec.length >= 0) dataSpec.length else 1

        if (playerCache.isCached(mediaId, dataSpec.position, length)) {
            return@Factory dataSpec
        }

        songUrlCache[mediaId]?.takeIf { it.second < System.currentTimeMillis() }?.let {
            return@Factory dataSpec.withUri(it.first.toUri())
        }

        val playedFormat = runBlocking(Dispatchers.IO) { formatDao.format(mediaId).first() }
        val playerResponse = runBlocking(Dispatchers.IO) {
            YouTube.player(mediaId)
        }.getOrThrow()
        if (playerResponse.playabilityStatus.status != "OK") {
            throw PlaybackException(
                playerResponse.playabilityStatus.reason,
                null,
                PlaybackException.ERROR_CODE_REMOTE_ERROR
            )
        }

        val format = if (playedFormat != null) {
            playerResponse.streamingData?.adaptiveFormats?.find { it.itag == playedFormat.itag }
        } else {
            playerResponse.streamingData?.adaptiveFormats?.filter { it.isAudio }?.maxByOrNull {
                it.bitrate * when (audioQuality) {
                    AudioQuality.AUTO -> if (connectivityManager.isActiveNetworkMetered) LOW_QUALITY else HIGH_QUALITY
                    AudioQuality.HIGH -> HIGH_QUALITY
                    AudioQuality.LOW -> LOW_QUALITY
                } + (if (it.mimeType.startsWith("audio/webm")) 10240 else 0) // prefer opus stream
            }
        }!!.let {
            // Specify range to avoid YouTube's throttling
            it.copy(url = "${it.url}&range=0-${it.contentLength ?: 10000000}")
        }

        applicationScope.launch(Dispatchers.IO) {
            formatDao.upsert(
                FormatEntity(
                    id = mediaId,
                    itag = format.itag,
                    mimeType = format.mimeType.split(";")[0],
                    codecs = format.mimeType.split("codecs=")[1].removeSurrounding("\""),
                    bitrate = format.bitrate,
                    sampleRate = format.audioSampleRate,
                    contentLength = format.contentLength!!,
                    loudnessDb = playerResponse.playerConfig?.audioConfig?.loudnessDb
                )
            )
        }

        songUrlCache[mediaId] =
            format.url!! to playerResponse.streamingData!!.expiresInSeconds * 1000L
        dataSpec.withUri(format.url!!.toUri())
    }

    val downloadNotificationHelper =
        DownloadNotificationHelper(context, ExoDownloadService.CHANNEL_ID)

    val downloadManager: DownloadManager = DownloadManager(
        context, databaseProvider, downloadCache, dataSourceFactory, Executor(Runnable::run)
    ).apply {
        maxParallelDownloads = MAX_PARALLEL_DOWNLOADS_EXO.getInt(DefaultMaxParallelDownloadsExo)
        addListener(
            ExoDownloadService.TerminalStateNotificationHelper(
                context = context,
                notificationHelper = downloadNotificationHelper,
                nextNotificationId = ExoDownloadService.NOTIFICATION_ID + 1
            )
        )
    }

    val downloads = MutableStateFlow<Map<String, Download>>(emptyMap())

    fun getDownload(songId: String): Flow<Download?> = downloads.map { it[songId] }

    init {
        val downloadListener = object : DownloadManager.Listener {
            override fun onDownloadChanged(
                downloadManager: DownloadManager, download: Download, finalException: Exception?
            ) {
                downloads.update { map ->
                    map.toMutableMap().apply {
                        set(download.request.id, download)
                    }
                }
            }
        }
        val result = mutableMapOf<String, Download>()
        val cursor = downloadManager.downloadIndex.getDownloads()
        while (cursor.moveToNext()) {
            result[cursor.download.request.id] = cursor.download
        }
        downloads.value = result
        downloadManager.addListener(downloadListener)
    }

    companion object {
        private const val HIGH_QUALITY = 1
        private const val LOW_QUALITY = -1
    }
}