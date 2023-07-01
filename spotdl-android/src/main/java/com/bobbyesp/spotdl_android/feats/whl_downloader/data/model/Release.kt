package com.bobbyesp.spotdl_android.feats.whl_downloader.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Release(
    @SerialName("comment_text") val commentText: String,
    val digests: Digests,
    val downloads: Int,
    val filename: String,
    @SerialName("has_sig") val hasSig: Boolean,
    @SerialName("md5_digest") val md5Digest: String,
    val packagetype: String,
    @SerialName("python_version") val pythonVersion: String,
    @SerialName("requires_python") val requiresPython: String?,
    val size: Int,
    @SerialName("upload_time") val uploadTime: String,
    @SerialName("upload_time_iso_8601") val uploadTimeIso8601: String,
    val url: String,
    val yanked: Boolean,
    @SerialName("yanked_reason") val yankedReason: String?
)

