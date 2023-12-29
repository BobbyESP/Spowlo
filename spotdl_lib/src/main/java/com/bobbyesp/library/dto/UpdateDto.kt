package com.bobbyesp.library.dto

import kotlinx.serialization.Serializable

@Serializable
data class Release(
    val url: String = "",
    val assets_url: String = "",
    val upload_url: String = "",
    val html_url: String = "",
    val id: Long = 0,
    val author: Author = Author(),
    val node_id: String = "",
    val tag_name: String = "",
    val target_commitish: String = "",
    val name: String = "",
    val draft: Boolean = false,
    val prerelease: Boolean = false,
    val created_at: String = "",
    val published_at: String = "",
    val assets: List<Asset>
)

@Serializable
data class Author(
    val login: String = "",
    val id: Long = 0,
    val node_id: String = "",
    val avatar_url: String = "",
    val gravatar_id: String = "",
    val url: String = "",
    val html_url: String = "",
    val followers_url: String = "",
    val following_url: String = "",
    val gists_url: String = "",
    val starred_url: String = "",
    val subscriptions_url: String = "",
    val organizations_url: String = "",
    val repos_url: String = "",
    val events_url: String = "",
    val received_events_url: String = "",
    val type: String = "",
    val site_admin: Boolean = false
)

@Serializable
data class Asset(
    val url: String = "",
    val id: Long = 0,
    val node_id: String = "",
    val name: String = "",
    val label: String = "",
    val uploader: Uploader = Uploader(),
    val content_type: String = "",
    val state: String = "",
    val size: Long = 0,
    val download_count: Long = 0,
    val created_at: String = "",
    val updated_at: String = "",
    val browser_download_url: String = ""
)

@Serializable
data class Uploader(
    val login: String = "",
    val id: Long = 0,
    val node_id: String = "",
    val avatar_url: String = "",
    val gravatar_id: String = "",
    val url: String = "",
    val html_url: String = "",
    val followers_url: String = "",
    val following_url: String = "",
    val gists_url: String = "",
    val starred_url: String = "",
    val subscriptions_url: String = "",
    val organizations_url: String = "",
    val repos_url: String = "",
    val events_url: String = "",
    val received_events_url: String = "",
    val type: String = "",
    val site_admin: Boolean = false
)