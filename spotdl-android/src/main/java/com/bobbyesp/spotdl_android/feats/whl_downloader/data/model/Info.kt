package com.bobbyesp.spotdl_android.feats.whl_downloader.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Info(
    val author: String,
    @SerialName("author_email") val authorEmail: String,
    @SerialName("bugtrack_url") val bugtrackUrl: String?,
    val classifiers: List<String>,
    val description: String,
    @SerialName("description_content_type") val descriptionContentType: String,
    @SerialName("docs_url") val docsUrl: String?,
    @SerialName("download_url") val downloadUrl: String,
    val downloads: Downloads,
    @SerialName("home_page") val homePage: String,
    val keywords: String,
    val license: String,
    val maintainer: String,
    @SerialName("maintainer_email") val maintainerEmail: String,
    val name: String,
    @SerialName("package_url") val packageUrl: String,
    val platform: String?,
    @SerialName("project_url") val projectUrl: String,
    @SerialName("project_urls") val projectUrls: ProjectUrls,
    @SerialName("release_url") val releaseUrl: String,
    @SerialName("requires_dist") val requiresDist: List<String>,
    @SerialName("requires_python") val requiresPython: String,
    val summary: String,
    val version: String,
    val yanked: Boolean,
    @SerialName("yanked_reason") val yankedReason: String?
)
