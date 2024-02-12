package com.bobbyesp.spowlo.ui.common

import android.content.Context
import android.net.Uri
import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.LocalPlay
import androidx.compose.material.icons.rounded.Lyrics
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Terminal
import androidx.compose.ui.graphics.vector.ImageVector
import com.bobbyesp.spowlo.R
import com.bobbyesp.spowlo.data.local.model.SelectedSong
import com.bobbyesp.spowlo.features.spotifyApi.data.local.model.MetadataEntity
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

sealed class Route(
    val route: String,
    var title: String,
    val icon: ImageVector? = null,
) {
    //NAVIGATION HOSTS
    data object MainHost : Route("main_host", "")

    //NAVIGATORS
    data object HomeNavigator : Route("home_navigator", "", Icons.Rounded.Home)
    data object SearchNavigator : Route("search_navigator", "", Icons.Rounded.Search)
    data object UtilitiesNavigator : Route("utilities_navigator", "", Icons.Rounded.LocalPlay)
    data object ProfileNavigator : Route("profile_navigator", "", Icons.Rounded.Person)
    data object SettingsNavigator : Route("settings_navigator", "", Icons.Rounded.Settings)
    data object DownloaderTasksNavigator :
        Route("downloader_tasks_navigator", "", Icons.Rounded.Download)

    //ROUTES
    data object Home : Route("home", "", Icons.Rounded.Home)
    data object Notifications : Route("notifications", "", Icons.Rounded.Notifications)
    data object Utilities : Route("utilities", "", Icons.Rounded.LocalPlay)
    data object LyricsDownloader : Route("lyrics_downloader", "", Icons.Rounded.Lyrics)
    data object SelectedSongLyrics :
        Route("selected_song/{${NavArgs.SelectedSong.key}}", "", Icons.Rounded.Lyrics) {
        fun createRoute(selectedSong: SelectedSong) =
            "selected_song/${Uri.encode(Json.encodeToString<SelectedSong>(selectedSong))}"
    }

    data object TagEditor : Route("tag_editor", "", Icons.Rounded.Edit) {
        data object Editor : Route(
            "tag_editor/editor/{${NavArgs.TagEditorSelectedSong.key}}",
            "",
            Icons.Default.Edit
        ) {
            fun createRoute(selectedSong: SelectedSong) =
                "tag_editor/editor/${Uri.encode(Json.encodeToString<SelectedSong>(selectedSong))}"
        }
    }

    data object MiniplayerPage : Route("miniplayer", "", Icons.Rounded.MusicNote)
    data object Search : Route("search", "", Icons.Rounded.Search)
    data object Profile : Route("profile", "", Icons.Rounded.Person)

    data object MetadataEntityViewer : Route(
        "metadata_entity_viewer/{${NavArgs.MetadataEntitySelected.key}}",
        "",
        Icons.Rounded.MusicNote
    ) {
        fun createRoute(metadataEntity: MetadataEntity) =
            "metadata_entity_viewer/${Uri.encode(Json.encodeToString<MetadataEntity>(metadataEntity))}"
    }

    data object DownloaderTasks : Route("downloader_tasks", "", Icons.Rounded.Download)
    data object FullScreenLog :
        Route("fullscreen_log/{${NavArgs.DownloaderTaskId.key}}", "", Icons.Rounded.Terminal) {
        fun createRoute(downloaderTaskId: Int) = "fullscreen_log/$downloaderTaskId"
    }
    data object OnboardingPage : Route("onboarding_page", "")
    data object Settings : Route("settings_page", "", Icons.Rounded.Settings)

    class StringUtils(
        private val context: Context
    ) {
        fun getStringWithContext(@StringRes resId: Int): String {
            return context.getString(resId)
        }
    }

    companion object {
        private lateinit var applicationContext: Context

        fun initialize(applicationContext: Context) {
            this.applicationContext = applicationContext
            val stringUtils = StringUtils(applicationContext)

            OnboardingPage.title = stringUtils.getStringWithContext(R.string.onboarding)

            MainHost.title = stringUtils.getStringWithContext(R.string.app_name)

            HomeNavigator.title = stringUtils.getStringWithContext(R.string.home)
            SearchNavigator.title = stringUtils.getStringWithContext(R.string.search)
            UtilitiesNavigator.title = stringUtils.getStringWithContext(R.string.utilities)
            SettingsNavigator.title = stringUtils.getStringWithContext(R.string.settings)
            ProfileNavigator.title = stringUtils.getStringWithContext(R.string.profile)

            Settings.title = stringUtils.getStringWithContext(R.string.settings)

            Home.title = stringUtils.getStringWithContext(R.string.home)
            Search.title = stringUtils.getStringWithContext(R.string.search)
            Utilities.title = stringUtils.getStringWithContext(R.string.utilities)
            LyricsDownloader.title = stringUtils.getStringWithContext(R.string.lyrics_downloader)
            MiniplayerPage.title = stringUtils.getStringWithContext(R.string.miniplayer)
            TagEditor.title = stringUtils.getStringWithContext(R.string.id3_tag_editor)
            Profile.title = stringUtils.getStringWithContext(R.string.profile)

        }
    }
}

enum class NavArgs(val key: String) {
    SelectedSong(key = "selectedSong"),
    TagEditorSelectedSong(key = "tagEditorSelectedSong"),
    MetadataEntitySelected(key = "metadataEntitySelected"),
    DownloaderTaskId(key = "downloaderTaskId")
}